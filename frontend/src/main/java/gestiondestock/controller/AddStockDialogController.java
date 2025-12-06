package gestiondestock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestiondestock.model.Stock;
import gestiondestock.model.Produit;
import gestiondestock.model.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class AddStockDialogController {

    @FXML
    private ComboBox<Produit> cbProduits;
    @FXML
    private TextField txtQuantite;
    @FXML
    private TextField txtSeuil;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnAnnuler;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Endpoints API
    private static final String BASE_URL = "http://localhost:8080/api/stock";
    private static final String PRODUIT_URL = "http://localhost:8080/api/produits";

    private Consumer<Void> onSuccess;

    public void setOnSuccess(Consumer<Void> callback) {
        this.onSuccess = callback;
    }

    @FXML
    public void initialize() {
        loadProduits();
    }

    private void loadProduits() {
        new Thread(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(PRODUIT_URL))
                        .header("Authorization", "Bearer " + Session.get().getToken())
                        .GET()
                        .build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() != 200) {
                    Platform.runLater(() -> showError("Impossible de charger les produits : " + res.statusCode()));
                    return;
                }

                List<Produit> produits = mapper.readValue(res.body(), new TypeReference<List<Produit>>() {
                });
                Platform.runLater(() -> cbProduits.getItems().setAll(produits));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur lors du chargement des produits : " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void handleAjouter() {
        Produit p = cbProduits.getValue();
        if (p == null) {
            showError("Veuillez sélectionner un produit");
            return;
        }

        int qte;
        int seuil;
        try {
            qte = Integer.parseInt(txtQuantite.getText());
            seuil = Integer.parseInt(txtSeuil.getText());
        } catch (NumberFormatException e) {
            showError("Quantité ou seuil invalide");
            return;
        }

        Stock.SimpleProduit simpleProduit = new Stock.SimpleProduit();
        simpleProduit.setId(java.util.UUID.fromString(p.getId()));
        simpleProduit.setNom(p.getNom());
        simpleProduit.setDescription(p.getDescription());
        simpleProduit.setPrixUnitaire(p.getPrixUnitaire());
        simpleProduit.setUrlImage(p.getUrlImage());

        Stock stock = new Stock();
        stock.setProduit(simpleProduit);
        stock.setQuantiteDisponible(qte);
        stock.setSeuilAlerte(seuil);

        new Thread(() -> {
            try {
                String json = mapper.writeValueAsString(stock);
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + Session.get().getToken())
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() != 200 && res.statusCode() != 201) {
                    Platform.runLater(() -> showError("Erreur HTTP : " + res.statusCode() + " - " + res.body()));
                    return;
                }

                if (onSuccess != null)
                    Platform.runLater(() -> onSuccess.accept(null));
                Platform.runLater(this::closeWindow);

            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur ajout : " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
