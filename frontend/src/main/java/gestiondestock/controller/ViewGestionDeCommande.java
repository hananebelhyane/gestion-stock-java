package gestiondestock.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.google.gson.Gson;

import gestiondestock.model.CommandeClient;
import gestiondestock.dto.CommandeDTO;
import gestiondestock.service.CommandeServiceClient;
import gestiondestock.controller.AddCommandeClientController;
import gestiondestock.controller.AddCommandeFournisseurController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import javafx.fxml.FXML;

public class ViewGestionDeCommande {

    @FXML
    private Label WelcomeLabel;

    @FXML
    private RadioButton rButton1; // Client Commande

    @FXML
    private RadioButton rButton2; // Fournisseur commande

    @FXML
    private TextField searchField;

    @FXML
    private Button btnRechercher;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnAjouter;

    @FXML
    private TableView<CommandeClient> commandeTable;

    @FXML
    private TableColumn<CommandeClient, String> colId;

    @FXML
    private TableColumn<CommandeClient, String> colDate;

    @FXML
    private TableColumn<CommandeClient, String> colStatut;

    @FXML
    private TableColumn<CommandeClient, Double> colMontant;

    @FXML
    private TableColumn<CommandeClient, String> colClient;

    @FXML
    private TableColumn<CommandeClient, Void> colAction;

    private final ObservableList<CommandeClient> commandeList = FXCollections.observableArrayList();
    private final CommandeServiceClient commandeService = new CommandeServiceClient();

    @FXML
    public void initialize() {
        // Configurer les colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientNom"));

        // Colonne Action avec bouton Supprimer
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");

            {
                btnDelete.setStyle("-fx-background-color: #000000ff; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 3;");
                btnDelete.setOnAction(event -> {
                    CommandeClient commande = getTableView().getItems().get(getIndex());
                    handleDelete(commande);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });

        commandeTable.setItems(commandeList);
    }

    public void setAdminData(String prenom, String nom) {
        if (WelcomeLabel != null) {
            WelcomeLabel.setText("Vous êtes : " + prenom + " " + nom);
        }
    }

    @FXML
    public void getCommande(ActionEvent event) {
        commandeList.clear(); // Vider le tableau

        // Activer tous les boutons et le champ de recherche
        enableControls(true);

        if (rButton1.isSelected()) {
            System.out.println("Chargement des commandes clients...");
            btnAjouter.setText("+ Ajouter Client");
            loadCommandesClient();
        } else if (rButton2.isSelected()) {
            System.out.println("Chargement des commandes fournisseurs...");
            btnAjouter.setText("+ Ajouter Fournisseur");
            loadCommandesFournisseur();
        }
    }

    private void loadCommandesClient() {
        List<CommandeDTO> dtos = commandeService.getCommandesClients();
        mapAndAddCommandes(dtos, true);
    }

    private void loadCommandesFournisseur() {
        List<CommandeDTO> dtos = commandeService.getCommandesFournisseurs();
        mapAndAddCommandes(dtos, false);
    }

    private void mapAndAddCommandes(List<CommandeDTO> dtos, boolean isClient) {
        for (CommandeDTO dto : dtos) {
            gestiondestock.model.CommandeClient cmd = new gestiondestock.model.CommandeClient();
            cmd.setId(dto.getId());
            cmd.setDateCommande(dto.getUnifiedDate());
            cmd.setStatut(dto.getStatut());
            cmd.setSeuilMax(dto.getSeuilMax());

            gestiondestock.model.CommandeClient.Client client = new gestiondestock.model.CommandeClient.Client();
            if (isClient && dto.getClient() != null) {
                client.setNom(dto.getClient().getNom());
                client.setPrenom(dto.getClient().getPrenom());
            } else if (!isClient && dto.getProduit() != null) {
                // For suppliers, we show product name in client column
                client.setNom(dto.getProduit().getNom());
                client.setPrenom("");
            } else {
                client.setNom("Inconnu");
            }
            cmd.setClient(client);
            
            commandeList.add(cmd);
        }
    }

    /**
     * Active ou désactive les contrôles (boutons + champ de recherche)
     */
    private void enableControls(boolean enable) {
        searchField.setDisable(!enable);
        btnRechercher.setDisable(!enable);
        btnRefresh.setDisable(!enable);
        btnAjouter.setDisable(!enable);
    }

    /**
     * Recherche dans la table en temps réel
     */
    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            commandeTable.setItems(commandeList);
            return;
        }

        ObservableList<CommandeClient> filtered = FXCollections.observableArrayList();
        for (CommandeClient cmd : commandeList) {
            boolean matches = false;

            if (cmd.getId() != null && cmd.getId().toLowerCase().contains(query)) {
                matches = true;
            }
            if (cmd.getStatut() != null && cmd.getStatut().toLowerCase().contains(query)) {
                matches = true;
            }
            if (cmd.getClientNom() != null && cmd.getClientNom().toLowerCase().contains(query)) {
                matches = true;
            }

            if (matches) {
                filtered.add(cmd);
            }
        }
        commandeTable.setItems(filtered);
    }

    /**
     * Bouton Rechercher (lance la recherche manuellement)
     */
    @FXML
    public void handleRechercher() {
        handleSearch();
    }

    /**
     * Bouton Rafraîchir
     */
    @FXML
    public void handleRefresh() {
        System.out.println("🔄 Rafraîchissement des données...");
        searchField.clear();
        commandeList.clear(); // Vider la liste avant de recharger
        if (rButton1.isSelected()) {
            loadCommandesClient();
        } else if (rButton2.isSelected()) {
            loadCommandesFournisseur();
        }
    }

    /**
     * Bouton Ajouter Client/Fournisseur
     */
    @FXML
    public void handleAjouter() {
        try {
            FXMLLoader loader;
            String title;
            Runnable callback;
            Parent root;

            if (rButton1.isSelected()) {
                // Ajouter commande client
                loader = new FXMLLoader(getClass().getResource("/fxml/add-commande-client.fxml"));
                root = loader.load();
                AddCommandeClientController controller = loader.getController();
                callback = () -> {
                    commandeList.clear();
                    loadCommandesClient();
                };
                controller.setOnCommandeCreated(callback);
                title = "Ajouter une commande client";
            } else if (rButton2.isSelected()) {
                // Ajouter commande fournisseur
                loader = new FXMLLoader(getClass().getResource("/fxml/add-commande-fournisseur.fxml"));
                root = loader.load();
                AddCommandeFournisseurController controller = loader.getController();
                callback = () -> {
                    commandeList.clear();
                    loadCommandesFournisseur();
                };
                controller.setOnCommandeCreated(callback);
                title = "Ajouter une commande fournisseur";
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Veuillez sélectionner un type de commande.");
                alert.showAndWait();
                return;
            }

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Supprimer une commande
     */
    private void handleDelete(CommandeClient commande) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la commande");
        confirmation.setContentText("Voulez-vous vraiment supprimer cette commande ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String endpoint = rButton1.isSelected() ? "clients" : "fournisseurs";
                    URL url = new URL("http://localhost:8080/api/commandes/" + endpoint + "/" + commande.getId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Accept", "application/json");
                    
                    // Add authorization header
                    String token = gestiondestock.model.Session.get().getToken();
                    if (token != null && !token.isEmpty()) {
                        conn.setRequestProperty("Authorization", "Bearer " + token);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200 || responseCode == 204) {
                        commandeList.remove(commande);
                        System.out.println("✅ Commande supprimée avec succès");
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + responseCode);
                        alert.showAndWait();
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }
}