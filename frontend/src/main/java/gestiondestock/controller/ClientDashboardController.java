package gestiondestock.controller;

import gestiondestock.dao.CartDAO;
import gestiondestock.model.*;
import gestiondestock.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientDashboardController {
    @FXML
    private BorderPane root;
    @FXML
    private VBox sidebar;
    @FXML
    private ImageView logoImage;
    @FXML
    private FlowPane productGrid;
    @FXML
    private HBox categoryFilterContainer;
    @FXML
    private Label cartBadge;

    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    // Node to save the initial catalogue view for sidebar navigation
    private Node catalogueView;

    @FXML
    public void initialize() {
        loadLogo();
        loadCategories();
        loadProducts(null);
        updateCartBadge();

        // FIXED: Saved the initial center node to allow navigation back
        this.catalogueView = root.getCenter();
    }

    // --- NAVIGATION ---

    @FXML
    public void loadAllProducts() {
        // Restores the catalogue view to the center of the BorderPane
        if (catalogueView != null) {
            root.setCenter(catalogueView);
        }
        loadProducts(null);
    }

    @FXML
    private void navigateToProfile() {
        try {
            Parent p = FXMLLoader.load(getClass().getResource("/fxml/client-profile-view.fxml"));
            root.setCenter(p);
        } catch (Exception e) {
            showError("Erreur lors de l'ouverture du profil", e);
        }
    }

    @FXML
    private void handleLogout() {
        Session.get().clear();
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(login));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- CART MANAGEMENT ---

    @FXML
    private void showCart() {
        Task<CartDAO.OrderResponse> task = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.getPanier();
            }
        };
        task.setOnSucceeded(e -> {
            updateCartFromOrder(task.getValue());
            Platform.runLater(this::showCartDialog);
        });
        task.setOnFailed(e -> showError("Erreur chargement panier", task.getException()));
        new Thread(task).start();
    }

    private void showCartDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Mon Panier");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(450);

        if (cartItems.isEmpty()) {
            content.getChildren().add(new Label("Votre panier est vide."));
        } else {
            for (CartItem item : cartItems) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label name = new Label(item.getProduit().getNom());
                name.setPrefWidth(150);
                Label qty = new Label("x" + item.getQuantite());
                Label price = new Label(String.format("%.2f DH", item.getTotal()));

                Button delBtn = new Button("🗑");
                delBtn.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
                delBtn.setOnAction(e -> {
                    dialog.close();
                    removeFromPanier(item);
                });

                row.getChildren().addAll(name, qty, price, new Region(), delBtn);
                HBox.setHgrow(row.getChildren().get(3), Priority.ALWAYS);
                content.getChildren().add(row);
            }

            Separator sep = new Separator();
            double totalVal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            Label totalLabel = new Label(String.format("Total: %.2f DH", totalVal));
            totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            // UI UPDATE: Validation button is now Black
            Button checkoutBtn = new Button("Confirmer la commande");
            checkoutBtn.setMaxWidth(Double.MAX_VALUE);
            checkoutBtn.setStyle(
                    "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
            checkoutBtn.setOnAction(e -> {
                dialog.close();
                performCheckout();
            });

            content.getChildren().addAll(sep, totalLabel, checkoutBtn);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void performCheckout() {
        Task<CartDAO.ConfirmationResponse> task = new Task<>() {
            @Override
            protected CartDAO.ConfirmationResponse call() {
                CartDAO.OrderResponse order = CartDAO.getPanier();
                return (order == null) ? null : CartDAO.confirmOrder(order.id);
            }
        };
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                new Alert(Alert.AlertType.INFORMATION, "Commande confirmée !").show();
                updateCartFromOrder(null);
            }
        });
        new Thread(task).start();
    }

    private void removeFromPanier(CartItem item) {
        Task<CartDAO.OrderResponse> task = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.removePanierItem(item.getProduit().getId());
            }
        };
        task.setOnSucceeded(e -> {
            updateCartFromOrder(task.getValue());
            showCart();
        });
        new Thread(task).start();
    }

    // --- QUANTITY DIALOG ---

    private void showQuantityDialog(Produit produit) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Ajouter au panier");
        dialog.setHeaderText("Produit : " + produit.getNom());

        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        Label lblPrix = new Label(String.format("Prix unitaire : %.2f DH", produit.getPrixUnitaire()));

        HBox qtyBox = new HBox(10);
        qtyBox.setAlignment(Pos.CENTER_LEFT);
        Label lblQtyText = new Label("Quantité :");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 100, 1);
        qtySpinner.setEditable(true);
        qtyBox.getChildren().addAll(lblQtyText, qtySpinner);

        Label lblTotal = new Label(String.format("Total : %.2f DH", produit.getPrixUnitaire()));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #000000;");

        qtySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            double total = newVal * produit.getPrixUnitaire();
            lblTotal.setText(String.format("Total : %.2f DH", total));
        });

        content.getChildren().addAll(lblPrix, qtyBox, lblTotal);
        dialog.getDialogPane().setContent(content);

        // UI UPDATE: Confirmation button is Black
        Button btnConfirm = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        btnConfirm.setStyle(
                "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        dialog.setResultConverter(btn -> btn == confirmButtonType ? qtySpinner.getValue() : null);

        dialog.showAndWait().ifPresent(quantity -> {
            Task<CartDAO.OrderResponse> task = new Task<>() {
                @Override
                protected CartDAO.OrderResponse call() {
                    return CartDAO.addPanierItem(produit.getId(), quantity);
                }
            };
            task.setOnSucceeded(e -> updateCartBadge());
            task.setOnFailed(e -> showError("Erreur ajout panier", task.getException()));
            new Thread(task).start();
        });
    }

    // --- UTILS & DATA LOADING ---

    private void updateCartFromOrder(CartDAO.OrderResponse order) {
        if (order == null || order.lignesCommande == null) {
            cartItems.clear();
        } else {
            List<CartItem> items = order.lignesCommande.stream().map(l -> {
                Produit p = new Produit();
                p.setId(l.produitId);
                p.setNom(l.produitNom);
                p.setPrixUnitaire(l.prixUnitaire);
                return new CartItem(p, l.quantite);
            }).collect(Collectors.toList());
            cartItems.setAll(items);
        }
        updateCartBadge();
    }

    private void updateCartBadge() {
        Task<CartDAO.OrderResponse> task = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.getPanier();
            }
        };
        task.setOnSucceeded(e -> {
            CartDAO.OrderResponse order = task.getValue();
            int count = (order != null && order.lignesCommande != null)
                    ? order.lignesCommande.stream().mapToInt(l -> l.quantite).sum()
                    : 0;
            Platform.runLater(() -> {
                if (cartBadge != null) {
                    cartBadge.setText(String.valueOf(count));
                    cartBadge.setVisible(count > 0);
                }
            });
        });
        new Thread(task).start();
    }

    private void loadCategories() {
        Task<List<Categorie>> task = new Task<>() {
            @Override
            protected List<Categorie> call() throws Exception {
                return categorieService.getAllCategories();
            }
        };
        task.setOnSucceeded(e -> {
            categoryFilterContainer.getChildren().clear();
            categoryFilterContainer.getChildren().add(createCategoryBtn("Tous", null, true));
            for (Categorie c : task.getValue()) {
                categoryFilterContainer.getChildren().add(createCategoryBtn(c.getNom(), c.getId(), false));
            }
        });
        new Thread(task).start();
    }

    private Button createCategoryBtn(String name, String id, boolean isSelected) {
        Button btn = new Button(name);
        // UI UPDATE: Uniform shape for all categories
        String baseStyle = "-fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand; -fx-font-weight: bold; -fx-min-width: 80;";
        String inactiveStyle = baseStyle
                + "-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 20; -fx-text-fill: #4B5563;";
        String activeStyle = baseStyle + "-fx-background-color: #000000; -fx-text-fill: white;";

        btn.setStyle(isSelected ? activeStyle : inactiveStyle);

        btn.setOnAction(e -> {
            // UI UPDATE: Highlight current selected category in Black
            categoryFilterContainer.getChildren().forEach(n -> ((Button) n).setStyle(inactiveStyle));
            btn.setStyle(activeStyle);
            loadProducts(id);
        });
        return btn;
    }

    private void loadProducts(String catId) {
        productGrid.getChildren().clear();
        Task<List<Produit>> task = new Task<>() {
            @Override
            protected List<Produit> call() throws Exception {
                return produitService.getAllProduits(catId);
            }
        };
        task.setOnSucceeded(e -> {
            for (Produit p : task.getValue())
                productGrid.getChildren().add(buildProductCard(p));
        });
        new Thread(task).start();
    }

    private VBox buildProductCard(Produit p) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setPrefWidth(240);
        // Original style with white background and dropshadow
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4); -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 12;");

        Label name = new Label(p.getNom());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #111827;");
        Label price = new Label(String.format("%.2f DH", p.getPrixUnitaire()));
        price.setStyle("-fx-font-weight: 800; -fx-font-size: 16px; -fx-text-fill: #374151;");

        Button addBtn = new Button("Ajouter au panier");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle(
                "-fx-background-color: #000000; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10; -fx-font-weight: bold; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showQuantityDialog(p));

        card.getChildren().addAll(name, price, addBtn);
        return card;
    }

    private void loadLogo() {
        var url = getClass().getResource("/assets/gs.png");
        if (url != null)
            logoImage.setImage(new Image(url.toExternalForm(), true));
    }

    private void showError(String msg, Throwable t) {
        if (t != null)
            t.printStackTrace();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.show();
        });
    }
}