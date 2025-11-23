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
import gestiondestock.controller.AddCommandeClientController;
import gestiondestock.controller.AddCommandeFournisseurController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private TableColumn<CommandeClient, LocalDateTime> colDate;

    @FXML
    private TableColumn<CommandeClient, String> colStatut;

    @FXML
    private TableColumn<CommandeClient, Double> colMontant;

    @FXML
    private TableColumn<CommandeClient, String> colClient;

    @FXML
    private TableColumn<CommandeClient, Void> colAction;

    private final ObservableList<CommandeClient> commandeList = FXCollections.observableArrayList();

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
        try {
            URL url = new URL("http://localhost:8080/api/commandes/clients");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    System.out.println("JSON reçu: " + response.toString());

                    Gson gson = new Gson();
                    CommandeClient[] commandesArray = gson.fromJson(response.toString(), CommandeClient[].class);
                    List<CommandeClient> commandes = List.of(commandesArray);

                    commandeList.addAll(commandes);
                    System.out.println("✅ Commandes chargées : " + commandes.size());

                    for (CommandeClient cmd : commandes) {
                        System.out.println("Client: " + cmd.getClientNom()
                                + " | Date: " + cmd.getDateCommande()
                                + " | Statut: " + cmd.getStatut());
                    }
                }
            } else {
                System.out.println("❌ Erreur HTTP : " + responseCode);
            }
            conn.disconnect();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement des commandes : " + e.getMessage());
        }
    }

    private void loadCommandesFournisseur() {
        try {
            URL url = new URL("http://localhost:8080/api/commandes/fournisseurs");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code (fournisseurs): " + responseCode);

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    System.out.println("JSON fournisseurs reçu: " + response.toString());

                    // Parser manuel pour adapter la structure CommandeFournisseur -> CommandeClient (affichage)
                    com.google.gson.JsonArray arr = com.google.gson.JsonParser.parseString(response.toString()).getAsJsonArray();
                    for (com.google.gson.JsonElement el : arr) {
                        com.google.gson.JsonObject obj = el.getAsJsonObject();
                        String id = obj.has("id") && !obj.get("id").isJsonNull() ? obj.get("id").getAsString() : "";
                        String date = "";
                        if (obj.has("commandeDate") && !obj.get("commandeDate").isJsonNull()) {
                            date = obj.get("commandeDate").getAsString();
                        } else if (obj.has("commande_date") && !obj.get("commande_date").isJsonNull()) {
                            date = obj.get("commande_date").getAsString();
                        }
                        String statut = obj.has("statut") && !obj.get("statut").isJsonNull() ? obj.get("statut").getAsString() : "";

                        // Récupérer le nom du produit (pour l'afficher dans la colonne "Client" comme fallback)
                        String produitNom = "";
                        if (obj.has("produit") && obj.get("produit").isJsonObject()) {
                            com.google.gson.JsonObject p = obj.getAsJsonObject("produit");
                            if (p.has("nom") && !p.get("nom").isJsonNull()) {
                                produitNom = p.get("nom").getAsString();
                            }
                        }

                        // Construire un objet CommandeClient adapté pour l'affichage
                        gestiondestock.model.CommandeClient.Client fauxClient = new gestiondestock.model.CommandeClient.Client();
                        fauxClient.setNom(produitNom);
                        gestiondestock.model.CommandeClient nouvelle = new gestiondestock.model.CommandeClient();
                        nouvelle.setId(id);
                        nouvelle.setClient(fauxClient);
                        nouvelle.setDateCommande(date);
                        nouvelle.setStatut(statut);
                        nouvelle.setSeuilMax(0); // Pas de seuil max pour les fournisseurs

                        commandeList.add(nouvelle);
                    }

                    System.out.println("✅ Commandes fournisseurs chargées : " + arr.size());
                }
            } else {
                System.out.println("❌ Erreur HTTP (fournisseurs) : " + responseCode);
            }
            conn.disconnect();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement des commandes fournisseurs : " + e.getMessage());
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