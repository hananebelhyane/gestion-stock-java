package gestiondestock.controller;

import gestiondestock.dao.CartDAO;
import gestiondestock.model.CartItem;
import gestiondestock.model.Categorie;
import gestiondestock.model.Produit;
import gestiondestock.model.Session;
import gestiondestock.service.CategorieService;
import gestiondestock.service.ProduitService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientDashboardController {

    @FXML
    private BorderPane root;
    @FXML
    private VBox sidebar;
    @FXML
    private Button sidebarToggleBtn;
    @FXML
    private ImageView logoImage;
    @FXML
    private FlowPane productGrid;
    @FXML
    private ComboBox<Categorie> categoryFilter;
    @FXML
    private Label cartBadge;

    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadLogo();
        setupCategoryFilter();
        loadCategories();
        loadProducts(null);
        refreshCartFromServer(false);
        updateCartBadge();
    }

    @FXML
    private void toggleSidebar() {
        if (sidebar == null || sidebarToggleBtn == null)
            return;
        boolean willShow = !sidebar.isVisible();
        sidebar.setVisible(willShow);
        sidebar.setManaged(willShow);
        sidebarToggleBtn.setText(willShow ? "⟨" : "☰");
    }

    @FXML
    private void onCategoryChanged() {
        Categorie selected = categoryFilter != null ? categoryFilter.getValue() : null;
        String categoryId = selected != null ? selected.getId() : null;
        loadProducts(categoryId);
    }

    @FXML
    private void showCart() {
        Task<CartDAO.OrderResponse> refreshTask = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.getPanier();
            }
        };

        refreshTask.setOnSucceeded(e -> {
            updateCartFromOrder(refreshTask.getValue());
            Platform.runLater(this::showCartDialog);
        });
        refreshTask.setOnFailed(e -> {
            showError("Impossible de charger le panier", refreshTask.getException());
            Platform.runLater(this::showCartDialog);
        });

        new Thread(refreshTask, "panier-refresh-before-dialog").start();
    }

    private void showCartDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Panier");

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        if (cartItems.isEmpty()) {
            Label empty = new Label("Votre panier est vide.");
            empty.setStyle("-fx-text-fill: #6b7280;");
            content.getChildren().add(empty);
        } else {
            for (CartItem item : cartItems) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label name = new Label(item.getProduit().getNom());
                name.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(name, Priority.ALWAYS);

                Label qty = new Label("x" + item.getQuantite());
                Label total = new Label(String.format("%.2f DH", item.getTotal()));

                Button remove = new Button("🗑");
                remove.setOnAction(e -> {
                    removeFromPanier(item, dialog);
                });

                row.getChildren().addAll(name, qty, total, remove);
                content.getChildren().add(row);
            }

            double totalPrice = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            Label totalLabel = new Label(String.format("Total: %.2f DH", totalPrice));
            totalLabel.setStyle("-fx-font-weight: bold;");
            content.getChildren().add(totalLabel);

            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_RIGHT);

            Button validateBtn = new Button("Valider le panier");
            validateBtn.setOnAction(e -> {
                dialog.close();
                performCheckout();
            });

            actions.getChildren().add(validateBtn);
            content.getChildren().add(actions);
        }

        pane.setContent(content);
        dialog.showAndWait();
    }

    private void performCheckout() {
        Task<CartDAO.ConfirmationResponse> task = new Task<>() {
            @Override
            protected CartDAO.ConfirmationResponse call() {
                CartDAO.OrderResponse order = CartDAO.getPanier();
                if (order == null || order.lignesCommande == null || order.lignesCommande.isEmpty()) {
                    return null;
                }
                return CartDAO.confirmOrder(order.id);
            }
        };

        task.setOnSucceeded(ev -> {
            CartDAO.ConfirmationResponse cf = task.getValue();
            if (cf == null || cf.facture_data == null) {
                showError("Votre panier est vide", null);
                return;
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Panier");
            ok.setHeaderText("Panier validé avec succès");
            ok.setContentText("Votre commande a été confirmée et la facture a été générée.");
            ok.showAndWait();

            cartItems.clear();
            updateCartBadge();
            showFactureDialog(cf.facture_data);
        });
        task.setOnFailed(ev -> showError("Échec de la validation du panier", task.getException()));
        new Thread(task, "panier-validate-task").start();
    }

    private void showOrderConfirmation(CartDAO.OrderResponse order) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de commande");
        confirm.setHeaderText("Confirmer et générer la facture ?");
        confirm.setContentText(String.format("Commande: %s\nTotal: %.2f DH", order.id, order.montantTotal));

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                Task<CartDAO.ConfirmationResponse> task = new Task<>() {
                    @Override
                    protected CartDAO.ConfirmationResponse call() {
                        return CartDAO.confirmOrder(order.id);
                    }
                };

                task.setOnSucceeded(e -> {
                    CartDAO.ConfirmationResponse cf = task.getValue();
                    if (cf == null || cf.facture_data == null) {
                        showError("Échec de la confirmation", null);
                        return;
                    }
                    cartItems.clear();
                    updateCartBadge();
                    showFactureDialog(cf.facture_data);
                });
                task.setOnFailed(e -> showError("Échec de la confirmation", task.getException()));
                new Thread(task, "confirm-task").start();
            }
        });
    }

    private void showFactureDialog(CartDAO.FactureResponse facture) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Facture");
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.CLOSE);

        VBox box = new VBox(10);
        box.setPadding(new Insets(12));
        box.getChildren().addAll(
                new Label("Facture: " + facture.id),
                new Label("Commande: " + facture.commandeId),
                new Label(String.format("Montant: %.2f DH", facture.montantTotal)),
                new Label("Payée: " + (facture.estPayee ? "Oui" : "Non")));

        pane.setContent(box);
        dialog.showAndWait();
    }

    private void loadLogo() {
        if (logoImage != null) {
            String[] candidates = new String[] { "/assets/gs.png" };
            for (String path : candidates) {
                var url = getClass().getResource(path);
                if (url != null) {
                    logoImage.setImage(new Image(url.toExternalForm(), true));
                    return;
                }
            }
            logoImage.setManaged(false);
            logoImage.setVisible(false);
        }
    }

    private void setupCategoryFilter() {
        if (categoryFilter == null)
            return;
        categoryFilter.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        categoryFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Toutes les catégories" : item.getNom());
            }
        });
    }

    private void loadCategories() {
        Task<List<Categorie>> task = new Task<>() {
            @Override
            protected List<Categorie> call() throws Exception {
                return categorieService.getAllCategories();
            }
        };

        task.setOnSucceeded(e -> {
            List<Categorie> categories = task.getValue();
            categories.sort(Comparator.comparing(Categorie::getNom, String.CASE_INSENSITIVE_ORDER));
            categoryFilter.getItems().clear();
            categoryFilter.getItems().add(null);
            categoryFilter.getItems().addAll(categories);
            categoryFilter.setValue(null);
        });
        task.setOnFailed(e -> showError("Impossible de charger les catégories", task.getException()));
        new Thread(task, "categories-loader").start();
    }

    private void loadProducts(String categoryId) {
        Task<List<Produit>> task = new Task<>() {
            @Override
            protected List<Produit> call() throws Exception {
                return produitService.getAllProduits(categoryId);
            }
        };
        task.setOnSucceeded(e -> renderProducts(task.getValue()));
        task.setOnFailed(e -> showError("Impossible de charger les produits", task.getException()));
        new Thread(task, "products-loader").start();
    }

    private void renderProducts(List<Produit> produits) {
        if (productGrid == null)
            return;
        productGrid.getChildren().clear();

        if (produits == null || produits.isEmpty()) {
            Label empty = new Label("Aucun produit pour cette catégorie.");
            empty.setStyle("-fx-text-fill: #6b7280;");
            productGrid.getChildren().add(empty);
            return;
        }

        for (Produit produit : produits) {
            productGrid.getChildren().add(buildCard(produit));
        }
    }

    private VBox buildCard(Produit produit) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(196);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(true);
        if (produit.getUrlImage() != null && !produit.getUrlImage().isBlank()) {
            try {
                imageView.setImage(new Image(produit.getUrlImage(), true));
            } catch (Exception ignored) {
                var fallback = getClass().getResourceAsStream("/assets/placeholder.png");
                if (fallback != null) {
                    imageView.setImage(new Image(fallback));
                }
            }
        } else {
            var fallback = getClass().getResourceAsStream("/assets/placeholder.png");
            if (fallback != null) {
                imageView.setImage(new Image(fallback));
            }
        }

        Label name = new Label(produit.getNom());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label price = new Label(
                String.format("%.2f DH", produit.getPrixUnitaire() != null ? produit.getPrixUnitaire() : 0.0));
        price.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        Label categoryLabel = new Label(produit.getCategorie() != null ? produit.getCategorie().getNom() : "");
        categoryLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button infoBtn = new Button("ℹ Info");
        infoBtn.setOnAction(e -> showProductInfo(produit));

        Button addBtn = new Button("+ Ajouter au panier");
        addBtn.setDefaultButton(true);
        addBtn.setOnAction(e -> addToCart(produit));

        actions.getChildren().addAll(infoBtn, addBtn);
        card.getChildren().addAll(imageView, name, price, categoryLabel, actions);
        return card;
    }

    private void showProductInfo(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produit");
        alert.setHeaderText(produit.getNom());
        String description = Optional.ofNullable(produit.getDescription()).orElse("Aucune description");
        String categorie = produit.getCategorie() != null ? produit.getCategorie().getNom() : "N/A";
        String fournisseur = produit.getFournisseur() != null ? produit.getFournisseur().getNom() : "N/A";
        alert.setContentText(
                "Prix: " + String.format("%.2f DH", produit.getPrixUnitaire() != null ? produit.getPrixUnitaire() : 0.0)
                        + "\nCatégorie: " + categorie
                        + "\nFournisseur: " + fournisseur
                        + "\n\n" + description);
        alert.showAndWait();
    }

    private void addToCart(Produit produit) {
        if (produit == null || produit.getId() == null) {
            showError("Produit invalide", null);
            return;
        }

        Task<CartDAO.OrderResponse> task = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.addPanierItem(produit.getId(), 1);
            }
        };

        task.setOnSucceeded(e -> {
            CartDAO.OrderResponse updated = task.getValue();
            if (updated == null) {
                showError("Impossible d'ajouter au panier", null);
                return;
            }
            updateCartFromOrder(updated);
        });
        task.setOnFailed(e -> showError("Impossible d'ajouter au panier", task.getException()));
        new Thread(task, "panier-add-task").start();
    }

    private void removeFromPanier(CartItem item, Dialog<Void> dialog) {
        if (item == null || item.getProduit() == null || item.getProduit().getId() == null) {
            return;
        }

        Task<CartDAO.OrderResponse> task = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.removePanierItem(item.getProduit().getId());
            }
        };

        task.setOnSucceeded(e -> {
            updateCartFromOrder(task.getValue());
            dialog.close();
            showCart();
        });
        task.setOnFailed(e -> showError("Impossible de supprimer l'article", task.getException()));
        new Thread(task, "panier-remove-task").start();
    }

    private void refreshCartFromServer(boolean showErrors) {
        Task<CartDAO.OrderResponse> task = new Task<>() {
            @Override
            protected CartDAO.OrderResponse call() {
                return CartDAO.getPanier();
            }
        };

        task.setOnSucceeded(e -> updateCartFromOrder(task.getValue()));
        task.setOnFailed(e -> {
            if (showErrors) {
                showError("Impossible de charger le panier", task.getException());
            }
        });
        new Thread(task, "panier-refresh-task").start();
    }

    private void updateCartFromOrder(CartDAO.OrderResponse order) {
        Platform.runLater(() -> {
            cartItems.setAll(toCartItems(order));
            updateCartBadge();
        });
    }

    private List<CartItem> toCartItems(CartDAO.OrderResponse order) {
        if (order == null || order.lignesCommande == null) {
            return List.of();
        }
        return order.lignesCommande.stream()
                .filter(l -> l != null && l.produitId != null)
                .map(l -> {
                    Produit p = new Produit();
                    p.setId(l.produitId);
                    p.setNom(l.produitNom);
                    p.setPrixUnitaire(l.prixUnitaire);
                    return new CartItem(p, l.quantite);
                })
                .filter(ci -> ci.getQuantite() > 0)
                .collect(Collectors.toList());
    }

    private void updateCartBadge() {
        if (cartBadge == null)
            return;
        int total = cartItems.stream().mapToInt(CartItem::getQuantite).sum();
        cartBadge.setText(String.valueOf(total));
        cartBadge.setVisible(total > 0);
        cartBadge.setManaged(true);
    }

    private void showError(String message, Throwable ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(message);
            alert.setContentText(ex != null ? ex.getMessage() : message);
            alert.showAndWait();
        });
    }

    @FXML
    private void showLastFacture() {
        Task<CartDAO.FactureResponse> task = new Task<>() {
            @Override
            protected CartDAO.FactureResponse call() {
                List<CartDAO.OrderResponse> history = CartDAO.getOrderHistory();
                if (history == null || history.isEmpty()) {
                    return null;
                }

                // Try most recent first
                for (int i = history.size() - 1; i >= 0; i--) {
                    CartDAO.OrderResponse order = history.get(i);
                    if (order == null || order.id == null) {
                        continue;
                    }
                    String statut = order.statut != null ? order.statut : "";
                    if (!statut.equalsIgnoreCase("confirmee") && !statut.equalsIgnoreCase("confirmée")) {
                        continue;
                    }
                    CartDAO.FactureResponse facture = CartDAO.getFacture(order.id);
                    if (facture != null) {
                        return facture;
                    }
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            CartDAO.FactureResponse facture = task.getValue();
            if (facture == null) {
                showError("Aucune facture trouvée", null);
                return;
            }
            showFactureDialog(facture);
        });
        task.setOnFailed(e -> showError("Impossible de charger la facture", task.getException()));
        new Thread(task, "last-facture-task").start();
    }

    @FXML
    private void navigateToProfile() {
        try {
            Parent profileRoot = FXMLLoader.load(getClass().getResource("/fxml/client-profile-view.fxml"));
            Stage stage = (Stage) root.getScene().getWindow();
            Scene scene = new Scene(profileRoot);
            var css = getClass().getResource("/styles/client-profile-style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle("Profil Client");
        } catch (Exception e) {
            showError("Erreur lors de l'ouverture du profil", e);
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Voulez-vous vous déconnecter ?");
        confirm.setContentText("Votre session sera fermée.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Session.get().clear();
                try {
                    Stage stage = (Stage) root.getScene().getWindow();
                    Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                    Scene scene = new Scene(loginRoot);
                    var css = getClass().getResource("/styles/login.css");
                    if (css != null) {
                        scene.getStylesheets().add(css.toExternalForm());
                    }
                    stage.setScene(scene);
                } catch (Exception e) {
                    showError("Erreur lors de la déconnexion", e);
                }
            }
        });
    }
}
