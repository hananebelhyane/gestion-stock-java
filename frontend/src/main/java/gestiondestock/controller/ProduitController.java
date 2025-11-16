package gestiondestock.controller;

import gestiondestock.dao.ProduitDAO;
import gestiondestock.model.Produit;
import gestiondestock.model.Categorie;
import gestiondestock.model.Fournisseur;
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

    private final ObservableList<Produit> produitsList = FXCollections.observableArrayList();
    private FilteredList<Produit> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadProduits();
        setupSearchFilter();
        setupActionsColumn();
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colCategorie.setCellValueFactory(cellData -> {
            Categorie cat = cellData.getValue().getCategorie();
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        colFournisseur.setCellValueFactory(cellData -> {
            Fournisseur fourn = cellData.getValue().getFournisseur();
            return new SimpleStringProperty(fourn != null ? fourn.getNom() : "");
        });
    }

    private void loadProduits() {
        try {
            produitsList.clear();
            produitsList.addAll(ProduitDAO.getAll());
            tableProduits.setItems(produitsList);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des produits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSearchFilter() {
        filteredData = new FilteredList<>(produitsList, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produit -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (produit.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (produit.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (produit.getCategorie() != null && 
                           produit.getCategorie().getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (produit.getFournisseur() != null && 
                           produit.getFournisseur().getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        SortedList<Produit> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduits.comparatorProperty());
        tableProduits.setItems(sortedData);
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<Produit, Void>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.getStyleClass().add("action-button");
                deleteButton.getStyleClass().addAll("action-button", "action-button-delete");
                
                editButton.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    handleEditProduct(produit);
                });
                
                deleteButton.setOnAction(event -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    handleDeleteProduct(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteProduct(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le produit \"" + produit.getNom() + "\" ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Supprimer de la liste
                    produitsList.remove(produit);
                    showAlert("Succès", "Produit supprimé avec succès!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleExport() {
        try {
            ProduitDAO.exportToCSV("produits_export.csv");
            showAlert("Succès", "Exportation CSV réussie!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'exportation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshTable() {
        loadProduits();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}