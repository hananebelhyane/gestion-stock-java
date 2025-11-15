package gestiondestock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import com.google.gson.Gson;
import gestiondestock.model.CommandeClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

public class ViewController {

    @FXML
    private Label WelcomeLabel;

    @FXML
    private RadioButton rButton1; // Client Commande

    @FXML
    private RadioButton rButton2; // Fournisseur commande

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

    private final ObservableList<CommandeClient> commandeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientNom"));

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

        if (rButton1.isSelected()) {
            System.out.println("Chargement des commandes clients...");
            loadCommandesClient();
        } else if (rButton2.isSelected()) {
            System.out.println("Chargement des commandes fournisseurs...");
            loadCommandesFournisseur();
        }
    }

    private void loadCommandesClient() {
        try {
            URL url = new URL("http://localhost:8082/api/commandes/clients");
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
            URL url = new URL("http://localhost:8082/api/commandes/fournisseurs");
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
}
