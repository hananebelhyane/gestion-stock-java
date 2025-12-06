package gestiondestock.controller;

import gestiondestock.model.Produit;
import gestiondestock.model.Categorie;
import gestiondestock.model.Fournisseur;
import gestiondestock.service.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, String> colDescription;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, String> colFournisseur;
    @FXML private TableColumn<Produit, Void> colActions;
    @FXML private Button addButton;
    @FXML private Button exportButton;

    private final ObservableList<Produit> produitsList = FXCollections.observableArrayList();
    private FilteredList<Produit> filteredData;
    private final ProduitService produitService = new ProduitService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupActionsColumn();
        loadProduits();
        setupSearchFilter();
        setupButtonsStyle();
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colCategorie.setCellValueFactory(cell -> {
            Categorie cat = cell.getValue().getCategorie();
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        colFournisseur.setCellValueFactory(cell -> {
            Fournisseur f = cell.getValue().getFournisseur();
            return new SimpleStringProperty(f != null ? f.getNom() : "");
        });
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("🗑");

            {
                editButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

                editButton.setOnAction(event -> handleEditProduct(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> handleDeleteProduct(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new HBox(5, editButton, deleteButton));
            }
        });
    }

    private void loadProduits() {
        javafx.concurrent.Task<java.util.List<Produit>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<Produit> call() throws Exception {
                return produitService.getAllProduits();
            }
        };
        task.setOnSucceeded(evt -> {
            produitsList.clear();
            produitsList.addAll(task.getValue());
            tableProduits.setItems(produitsList);
        });
        task.setOnFailed(evt -> showAlert("Erreur", "Erreur chargement: " + task.getException().getMessage(), Alert.AlertType.ERROR));
        new Thread(task).start();
    }

    private void setupSearchFilter() {
        filteredData = new FilteredList<>(produitsList, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return p.getNom().toLowerCase().startsWith(newVal.toLowerCase());
            });
        });
        SortedList<Produit> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduits.comparatorProperty());
        tableProduits.setItems(sortedData);
    }

    private void setupButtonsStyle() {
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
        exportButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    @FXML
    private void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_produit.fxml"));
            Parent root = loader.load();
            AddProduitController controller = loader.getController();
            controller.setProduitController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Produit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleEditProduct(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_produit.fxml"));
            Parent root = loader.load();
            EditProduitController controller = loader.getController();
            controller.setProduit(produit);
            controller.setProduitController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier le Produit");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteProduct(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le produit \"" + produit.getNom() + "\" ?", ButtonType.OK, ButtonType.CANCEL);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        produitService.deleteProduit(produit.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(ev -> {
                    showAlert("Succès", "Produit supprimé avec succès!", Alert.AlertType.INFORMATION);
                    refreshTable();
                });
                task.setOnFailed(ev -> {
                    String msg = task.getException() != null ? task.getException().getMessage() : "";
                    if (msg.contains("stock disponible > 0") || msg.contains("référencé par des commandes") || msg.contains("HTTP 400")) {
                        Alert confirmForce = new Alert(Alert.AlertType.CONFIRMATION,
                                "Ce produit est lié au stock ou à des commandes. Forcer la suppression ?\n(Le stock associé sera supprimé)",
                                ButtonType.OK, ButtonType.CANCEL);
                        confirmForce.showAndWait().ifPresent(resp -> {
                            if (resp == ButtonType.OK) {
                                javafx.concurrent.Task<Void> forceTask = new javafx.concurrent.Task<>() {
                                    @Override
                                    protected Void call() throws Exception {
                                        produitService.deleteProduitForce(produit.getId());
                                        return null;
                                    }
                                };
                                forceTask.setOnSucceeded(e2 -> {
                                    showAlert("Succès", "Produit supprimé (force) avec succès!", Alert.AlertType.INFORMATION);
                                    refreshTable();
                                });
                                forceTask.setOnFailed(e2 -> showAlert("Erreur", "Échec de la suppression forcée: " + forceTask.getException().getMessage(), Alert.AlertType.ERROR));
                                new Thread(forceTask).start();
                            } else {
                                showAlert("Info", "Suppression annulée", Alert.AlertType.INFORMATION);
                            }
                        });
                    } else {
                        showAlert("Erreur", "Erreur suppression: " + msg, Alert.AlertType.ERROR);
                    }
                });
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void handleExport() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                produitService.exportProduits("produits_export.csv");
                return null;
            }
        };
        task.setOnSucceeded(evt -> showAlert("Succès", "Export CSV réussi!", Alert.AlertType.INFORMATION));
        task.setOnFailed(evt -> showAlert("Erreur", "Erreur export: " + task.getException().getMessage(), Alert.AlertType.ERROR));
        new Thread(task).start();
    }

    public void refreshTable() { loadProduits(); }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
