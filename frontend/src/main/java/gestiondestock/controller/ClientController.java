package gestiondestock.controller;

import gestiondestock.util.NavigationHelper;
import gestiondestock.model.ClientModel;
import gestiondestock.model.Session;
import gestiondestock.service.ClientService;
import gestiondestock.util.CSVExporter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());

    @FXML private TableView<ClientModel> tableClients;
    @FXML private TableColumn<ClientModel, String> colId;
    @FXML private TableColumn<ClientModel, String> colNom;
    @FXML private TableColumn<ClientModel, String> colPrenom;
    @FXML private TableColumn<ClientModel, String> colUsername;
    @FXML private TableColumn<ClientModel, String> colTelephone;
    @FXML private TableColumn<ClientModel, String> colAdresse;
    @FXML private TableColumn<ClientModel, Void> colActions;

    @FXML private TextField txtSearch;
    @FXML private Button btnAjouter;
    @FXML private Button btnRechercher;
    @FXML private Button btnRafraichir;
    @FXML private Button btnShowDeleted;
    @FXML private Button btnExporter;
    @FXML private Button btnLogout;
    @FXML private Label lblUser;
    // NOUVEAU : Bouton pour retourner au menu utilisateurs
    @FXML private Button btnRetourMenu;
    //NOUVEAU : Bouton pour naviguer vers magasiniers
    @FXML private Button btnNavigateMagasiniers;

    private final ClientService clientService = new ClientService();
    private final ObservableList<ClientModel> clientsList = FXCollections.observableArrayList();
    private boolean showingDeleted = false;

    @FXML
    public void initialize() {
        // Vérifier l'authentification au chargement
        if (!checkAuthentication()) {
            return;
        }
        
        // Afficher le nom de l'utilisateur connecté (si le label existe dans le FXML)
        if (lblUser != null) {
            String username = Session.get().getUsername();
            String role = Session.get().getRole();
            lblUser.setText("Connecté: " + username + " (" + role + ")");
        }
        
        setupTableColumns();
        loadClients();
        setupActionButtons();
        setupButtonHandlers();
        setupNavigationButtons();
    }
    /**
     * Configure les boutons de navigation
     */
    private void setupNavigationButtons() {
        if (btnRetourMenu != null) {
            btnRetourMenu.setOnAction(e -> handleRetourMenu());
        }
        
        if (btnNavigateMagasiniers != null) {
            btnNavigateMagasiniers.setOnAction(e -> handleNavigateMagasiniers());
        }
    }
    /**
     * Retour au menu des utilisateurs
     */
    @FXML
    private void handleRetourMenu() {
        NavigationHelper.returnToUsersMenu(tableClients);
    }
    
    /**
     * Navigation vers la gestion des magasiniers
     */
    @FXML
    private void handleNavigateMagasiniers() {
        NavigationHelper.navigateToMagasiniers(tableClients);
    }
    
    // Vérifier l'authentification
    private boolean checkAuthentication() {
        Session session = Session.get();
        if (session.getToken() == null || session.getRole() == null) {
            showAlert(Alert.AlertType.ERROR, "Non authentifié", 
                "Vous devez être connecté pour accéder à cette page.");
            redirectToLogin();
            return false;
        }
        
        if (!"ADMIN".equalsIgnoreCase(session.getRole())) {
            showAlert(Alert.AlertType.ERROR, "Accès refusé", 
                "Seuls les administrateurs peuvent accéder à cette page.");
            redirectToLogin();
            return false;
        }
        
        return true;
    }
    
    // Gérer la déconnexion (si le bouton existe dans le FXML)
    @FXML
    private void handleLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Déconnexion");
        confirmDialog.setHeaderText("Confirmer la déconnexion");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir vous déconnecter?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Session.get().clear();
            redirectToLogin();
        }
    }
    
    // Rediriger vers la page de login
    private void redirectToLogin() {
        try {
            Stage stage = (Stage) tableClients.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            var css = getClass().getResource("/css/login.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.setMaximized(false);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la redirection vers login", e);
        }
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
                    loadDeletedClients();
                } else {
                    btnShowDeleted.setText("Afficher supprimés");
                    btnShowDeleted.getStyleClass().remove("btn-show-active");
                    btnShowDeleted.getStyleClass().add("btn-secondary");
                    loadClients();
                }
            });
        }
        
        // Bouton Exporter
        if (btnExporter != null) {
            btnExporter.setOnAction(e -> handleExporter());
        }
    }

    private void setupTableColumns() {
        // Configuration des colonnes
        colNom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPrenom()));
        colUsername.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUsername()));
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
                    ClientModel client = getTableView().getItems().get(getIndex());
                    handleModifier(client);
                });

                btnDelete.setOnAction(event -> {
                    ClientModel client = getTableView().getItems().get(getIndex());
                    handleSupprimer(client);
                });
                
                btnRestore.setOnAction(event -> {
                    ClientModel client = getTableView().getItems().get(getIndex());
                    handleRestore(client);
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
        Dialog<ClientModel> dialog = createClientDialog("Ajouter un client", null);
        Optional<ClientModel> result = dialog.showAndWait();

        result.ifPresent(client -> {
            try {
                clientService.createClient(client);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès!");
                loadClients();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du client", ex);
                handleServiceError(ex);
            }
        });
    }

    private void handleModifier(ClientModel client) {
        Dialog<ClientModel> dialog = createClientDialog("Modifier le client", client);
        Optional<ClientModel> result = dialog.showAndWait();

        result.ifPresent(updatedClient -> {
            try {
                clientService.updateClient(client.getId(), updatedClient);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client modifié avec succès!");
                loadClients();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la modification du client", ex);
                handleServiceError(ex);
            }
        });
    }

    private void handleSupprimer(ClientModel client) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le client");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer " + client.getNom() + " " + client.getPrenom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                clientService.deleteClient(client.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé avec succès!");
                loadClients();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du client", ex);
                handleServiceError(ex);
            }
        }
    }
    
    private void handleRestore(ClientModel client) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Restaurer le client");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir restaurer " + client.getNom() + " " + client.getPrenom() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                clientService.restoreClient(client.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client restauré avec succès!");
                loadDeletedClients();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la restauration du client", ex);
                handleServiceError(ex);
            }
        }
    }

    @FXML
    private void handleRechercher() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            if (showingDeleted) {
                loadDeletedClients();
            } else {
                loadClients();
            }
            return;
        }

        try {
            clientsList.clear();
            clientsList.addAll(clientService.searchClients(keyword));
            tableClients.setItems(clientsList);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche", ex);
            handleServiceError(ex);
        }
    }

    @FXML
    private void handleRafraichir() {
        txtSearch.clear();
        if (showingDeleted) {
            loadDeletedClients();
        } else {
            loadClients();
        }
    }
    
    // NOUVELLE MÉTHODE : Export CSV
    @FXML
    private void handleExporter() {
        try {
            // Vérifier qu'il y a des données à exporter
            if (clientsList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Export", 
                    "Aucun client à exporter. La liste est vide.");
                return;
            }
            
            // Déterminer le nom du fichier selon la vue actuelle
            String fileName = showingDeleted ? 
                "clients_supprimes_" : "clients_actifs_";
            fileName += java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            
            // Exporter
            Stage stage = (Stage) tableClients.getScene().getWindow();
            File exportedFile = CSVExporter.exportClientsToCSV(clientsList, stage, fileName);
            
            if (exportedFile != null) {
                showAlert(Alert.AlertType.INFORMATION, "Export réussi", 
                    "Les clients ont été exportés avec succès !\n\n" +
                    "Fichier: " + exportedFile.getName() + "\n" +
                    "Emplacement: " + exportedFile.getParent() + "\n" +
                    "Nombre de lignes: " + clientsList.size());
            }
            
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.WARNING, "Export", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'export CSV", ex);
            showAlert(Alert.AlertType.ERROR, "Erreur d'export", 
                "Erreur lors de l'export: " + ex.getMessage());
        }
    }
    
    private void loadClients() {
        try {
            clientsList.clear();
            clientsList.addAll(clientService.getAllActiveClients());
            tableClients.setItems(clientsList);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des clients", ex);
            handleServiceError(ex);
        }
    }
    
    private void loadDeletedClients() {
        try {
            clientsList.clear();
            clientsList.addAll(clientService.getDeletedClients());
            tableClients.setItems(clientsList);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des clients supprimés", ex);
            handleServiceError(ex);
        }
    }
    
    // Gérer les erreurs d'authentification
    private void handleServiceError(Exception ex) {
        String message = ex.getMessage();
        if (message.contains("Session expirée") || message.contains("Non authentifié")) {
            showAlert(Alert.AlertType.ERROR, "Session expirée", 
                "Votre session a expiré. Veuillez vous reconnecter.");
            Session.get().clear();
            redirectToLogin();
        } else if (message.contains("Accès refusé") || message.contains("permissions")) {
            showAlert(Alert.AlertType.ERROR, "Accès refusé", message);
            Session.get().clear();
            redirectToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", message);
        }
    }

    private Dialog<ClientModel> createClientDialog(String title, ClientModel client) {
        Dialog<ClientModel> dialog = new Dialog<>();
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
        TextField txtUsername = new TextField();
        TextField txtTelephone = new TextField();
        TextField txtAdresse = new TextField();

        txtNom.setPromptText("Nom");
        txtPrenom.setPromptText("Prénom");
        txtUsername.setPromptText("Username");
        txtTelephone.setPromptText("+212 XXX-XXXXXX");
        txtAdresse.setPromptText("Adresse complète");

        if (client != null) {
            txtNom.setText(client.getNom());
            txtPrenom.setText(client.getPrenom());
            txtUsername.setText(client.getUsername());
            txtTelephone.setText(client.getTelephone());
            txtAdresse.setText(client.getAdresse());
        }

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(txtPrenom, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(txtUsername, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(txtTelephone, 1, 3);
        grid.add(new Label("Adresse:"), 0, 4);
        grid.add(txtAdresse, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateDialogFields(txtNom, txtPrenom, txtUsername, txtTelephone, txtAdresse)) {
                    ClientModel newClient = new ClientModel();
                    newClient.setNom(txtNom.getText().trim());
                    newClient.setPrenom(txtPrenom.getText().trim());
                    newClient.setUsername(txtUsername.getText().trim());
                    newClient.setTelephone(txtTelephone.getText().trim());
                    newClient.setAdresse(txtAdresse.getText().trim());
                    return newClient;
                }
            }
            return null;
        });

        return dialog;
    }

    private boolean validateDialogFields(TextField txtNom, TextField txtPrenom, TextField txtUsername,
                                        TextField txtTelephone, TextField txtAdresse) {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() ||
            txtUsername.getText().trim().isEmpty() || txtTelephone.getText().trim().isEmpty() ||
            txtAdresse.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Tous les champs sont obligatoires");
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