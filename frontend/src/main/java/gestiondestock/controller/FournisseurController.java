package gestiondestock.controller;

import gestiondestock.model.FournisseurModel;
import gestiondestock.service.FournisseurService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FournisseurController {

    private static final Logger LOGGER = Logger.getLogger(FournisseurController.class.getName());

    @FXML private TableView<FournisseurModel> tableFournisseurs;
    @FXML private TableColumn<FournisseurModel, String> colId;
    @FXML private TableColumn<FournisseurModel, String> colNom;
    @FXML private TableColumn<FournisseurModel, String> colPrenom;
    @FXML private TableColumn<FournisseurModel, String> colEmail;
    @FXML private TableColumn<FournisseurModel, String> colTelephone;
    @FXML private TableColumn<FournisseurModel, String> colAdresse;
    @FXML private TableColumn<FournisseurModel, Void> colActions;

    @FXML private TextField txtSearch;
    @FXML private Button btnAjouter;
    @FXML private Button btnRechercher;
    @FXML private Button btnRafraichir;
    @FXML private Button btnShowDeleted;
    @FXML private Button btnExporter;

    private final FournisseurService fournisseurService = new FournisseurService();
    private final ObservableList<FournisseurModel> fournisseursList = FXCollections.observableArrayList();
    private boolean showingDeleted = false;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadFournisseurs();
        setupActionButtons();
        setupButtonHandlers();
    }
    
    private void setupButtonHandlers() {
        // Bouton Afficher supprimés/actifs
        if (btnShowDeleted != null) {
            btnShowDeleted.setOnAction(e -> {
                showingDeleted = !showingDeleted;
                if (showingDeleted) {
                    btnShowDeleted.setText("Afficher actifs");
                    btnShowDeleted.getStyleClass().remove("btn-secondary");
                    btnShowDeleted.getStyleClass().add("btn-show-active");
                    loadDeletedFournisseurs();
                } else {
                    btnShowDeleted.setText("Afficher supprimés");
                    btnShowDeleted.getStyleClass().remove("btn-show-active");
                    btnShowDeleted.getStyleClass().add("btn-secondary");
                    loadFournisseurs();
                }
            });
        }
        
        // Bouton Exporter
        if (btnExporter != null) {
            btnExporter.setOnAction(e -> {
                showAlert(Alert.AlertType.INFORMATION, "Export", "Fonctionnalité d'export en cours de développement...");
            });
        }
    }

    private void setupTableColumns() {
        // Configuration des colonnes
        colNom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPrenom()));
        colEmail.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail()));
        colTelephone.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelephone()));
        colAdresse.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAdresse()));
        
        // Colonne ID - Afficher seulement les 8 premiers caractères
        colId.setCellValueFactory(cellData -> {
            UUID id = cellData.getValue().getId();
            return new SimpleStringProperty(id != null ? id.toString() : "");
        });
        
        colId.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.substring(0, Math.min(8, item.length())) + "...");
                }
            }
        });
    }

    private void setupActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final Button btnRestore = new Button("♻");
            private final HBox pane = new HBox(10);

            {
                // Style moderne des boutons d'action
                String buttonStyle = "-fx-background-color: white; " +
                                    "-fx-border-color: #e0e0e0; " +
                                    "-fx-border-width: 1; " +
                                    "-fx-border-radius: 6; " +
                                    "-fx-background-radius: 6; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 8 12 8 12; " +
                                    "-fx-font-size: 16; " +
                                    "-fx-min-width: 40; " +
                                    "-fx-min-height: 35;";
                
                String buttonHoverStyle = "-fx-background-color: #fafafa; " +
                                         "-fx-border-color: #c0c0c0; " +
                                         "-fx-border-width: 1; " +
                                         "-fx-border-radius: 6; " +
                                         "-fx-background-radius: 6; " +
                                         "-fx-cursor: hand; " +
                                         "-fx-padding: 8 12 8 12; " +
                                         "-fx-font-size: 16; " +
                                         "-fx-min-width: 40; " +
                                         "-fx-min-height: 35;";
                
                btnEdit.setStyle(buttonStyle);
                btnDelete.setStyle(buttonStyle);
                btnRestore.setStyle(buttonStyle);
                
                // Effets hover
                btnEdit.setOnMouseEntered(e -> btnEdit.setStyle(buttonHoverStyle));
                btnEdit.setOnMouseExited(e -> btnEdit.setStyle(buttonStyle));
                
                btnDelete.setOnMouseEntered(e -> btnDelete.setStyle(buttonHoverStyle));
                btnDelete.setOnMouseExited(e -> btnDelete.setStyle(buttonStyle));
                
                btnRestore.setOnMouseEntered(e -> btnRestore.setStyle(buttonHoverStyle));
                btnRestore.setOnMouseExited(e -> btnRestore.setStyle(buttonStyle));
                
                pane.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    FournisseurModel fournisseur = getTableView().getItems().get(getIndex());
                    handleModifier(fournisseur);
                });

                btnDelete.setOnAction(event -> {
                    FournisseurModel fournisseur = getTableView().getItems().get(getIndex());
                    handleSupprimer(fournisseur);
                });
                
                btnRestore.setOnAction(event -> {
                    FournisseurModel fournisseur = getTableView().getItems().get(getIndex());
                    handleRestore(fournisseur);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    pane.getChildren().clear();
                    if (showingDeleted) {
                        pane.getChildren().add(btnRestore);
                    } else {
                        pane.getChildren().addAll(btnEdit, btnDelete);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    @FXML
    private void handleAjouter() {
        Dialog<FournisseurModel> dialog = createFournisseurDialog("Ajouter un fournisseur", null);
        Optional<FournisseurModel> result = dialog.showAndWait();

        result.ifPresent(fournisseur -> {
            try {
                fournisseurService.createFournisseur(fournisseur);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur ajouté avec succès!");
                loadFournisseurs();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du fournisseur", ex);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + ex.getMessage());
            }
        });
    }

    private void handleModifier(FournisseurModel fournisseur) {
        Dialog<FournisseurModel> dialog = createFournisseurDialog("Modifier le fournisseur", fournisseur);
        Optional<FournisseurModel> result = dialog.showAndWait();

        result.ifPresent(updatedFournisseur -> {
            try {
                fournisseurService.updateFournisseur(fournisseur.getId(), updatedFournisseur);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur modifié avec succès!");
                loadFournisseurs();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la modification du fournisseur", ex);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + ex.getMessage());
            }
        });
    }

    private void handleSupprimer(FournisseurModel fournisseur) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le fournisseur");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer " + fournisseur.getNom() + " " + fournisseur.getPrenom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                fournisseurService.deleteFournisseur(fournisseur.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur supprimé avec succès!");
                loadFournisseurs();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du fournisseur", ex);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }
    
    private void handleRestore(FournisseurModel fournisseur) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Restaurer le fournisseur");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir restaurer " + fournisseur.getNom() + " " + fournisseur.getPrenom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                fournisseurService.restoreFournisseur(fournisseur.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur restauré avec succès!");
                loadDeletedFournisseurs();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la restauration du fournisseur", ex);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la restauration: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleRechercher() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            if (showingDeleted) {
                loadDeletedFournisseurs();
            } else {
                loadFournisseurs();
            }
            return;
        }

        try {
            fournisseursList.clear();
            fournisseursList.addAll(fournisseurService.searchFournisseurs(keyword));
            tableFournisseurs.setItems(fournisseursList);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche", ex);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + ex.getMessage());
        }
    }

    @FXML
    private void handleRafraichir() {
        txtSearch.clear();
        if (showingDeleted) {
            loadDeletedFournisseurs();
        } else {
            loadFournisseurs();
        }
    }
    
    private void loadFournisseurs() {
        try {
            fournisseursList.clear();
            fournisseursList.addAll(fournisseurService.getAllActiveFournisseurs());
            tableFournisseurs.setItems(fournisseursList);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des fournisseurs", ex);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement: " + ex.getMessage());
        }
    }
    
    private void loadDeletedFournisseurs() {
        try {
            fournisseursList.clear();
            fournisseursList.addAll(fournisseurService.getDeletedFournisseurs());
            tableFournisseurs.setItems(fournisseursList);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des fournisseurs supprimés", ex);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement: " + ex.getMessage());
        }
    }

    private Dialog<FournisseurModel> createFournisseurDialog(String title, FournisseurModel fournisseur) {
        Dialog<FournisseurModel> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNom = new TextField();
        TextField txtPrenom = new TextField();
        TextField txtEmail = new TextField();
        TextField txtTelephone = new TextField();
        TextField txtAdresse = new TextField();

        txtNom.setPromptText("Nom");
        txtPrenom.setPromptText("Prénom");
        txtEmail.setPromptText("exemple@email.com");
        txtTelephone.setPromptText("+212 XXX-XXXXXX");
        txtAdresse.setPromptText("Adresse complète");

        if (fournisseur != null) {
            txtNom.setText(fournisseur.getNom());
            txtPrenom.setText(fournisseur.getPrenom());
            txtEmail.setText(fournisseur.getEmail());
            txtTelephone.setText(fournisseur.getTelephone());
            txtAdresse.setText(fournisseur.getAdresse());
        }

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(txtPrenom, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(txtTelephone, 1, 3);
        grid.add(new Label("Adresse:"), 0, 4);
        grid.add(txtAdresse, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateDialogFields(txtNom, txtPrenom, txtEmail, txtTelephone, txtAdresse)) {
                    FournisseurModel newFournisseur = new FournisseurModel();
                    newFournisseur.setNom(txtNom.getText().trim());
                    newFournisseur.setPrenom(txtPrenom.getText().trim());
                    newFournisseur.setEmail(txtEmail.getText().trim());
                    newFournisseur.setTelephone(txtTelephone.getText().trim());
                    newFournisseur.setAdresse(txtAdresse.getText().trim());
                    return newFournisseur;
                }
            }
            return null;
        });

        return dialog;
    }

    private boolean validateDialogFields(TextField txtNom, TextField txtPrenom, TextField txtEmail, 
                                        TextField txtTelephone, TextField txtAdresse) {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() || txtTelephone.getText().trim().isEmpty() ||
            txtAdresse.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Tous les champs sont obligatoires");
            return false;
        }

        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Format d'email invalide");
            return false;
        }

        if (!txtTelephone.getText().matches("^[0-9+\\-\\s()]{10,20}$")) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Format de téléphone invalide");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}