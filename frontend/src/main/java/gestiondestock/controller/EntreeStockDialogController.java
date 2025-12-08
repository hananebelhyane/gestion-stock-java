package gestiondestock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestiondestock.model.EntreeStock;
import gestiondestock.model.Session;
import gestiondestock.model.Stock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class EntreeStockDialogController {

    private static final String BASE_URL = "http://localhost:8080/api/stock";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML private ComboBox<ProduitItem> cbProduit;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtCommandeFournisseur;
    @FXML private TextArea txtNotes;
    @FXML private Button btnEnregistrer;
    @FXML private Button btnAnnuler;
    @FXML private Label lblStockActuel;

    private Consumer<Void> onSuccessCallback;
    private UUID selectedProduitId;

    @FXML
    public void initialize() {
        chargerProduits();
        
        cbProduit.setOnAction(e -> {
            ProduitItem selected = cbProduit.getValue();
            if (selected != null) {
                afficherStockActuel(selected.getId());
            }
        });
    }

    public void setSelectedProduitId(UUID produitId) {
        this.selectedProduitId = produitId;
        if (cbProduit.getItems() != null && !cbProduit.getItems().isEmpty()) {
            for (ProduitItem item : cbProduit.getItems()) {
                if (item.getId().equals(produitId)) {
                    cbProduit.setValue(item);
                    afficherStockActuel(produitId);
                    break;
                }
            }
        }
    }

    public void setOnSuccess(Consumer<Void> callback) {
        this.onSuccessCallback = callback;
    }

    private void chargerProduits() {
        try {
            List<Stock> stocks = apiGetAllStock();
            List<ProduitItem> items = stocks.stream()
                .filter(s -> s.getProduit() != null)
                .map(s -> new ProduitItem(
                    s.getProduit().getId(), 
                    s.getProduit().getNom(),
                    s.getQuantiteDisponible()
                ))
                .toList();
            
            cbProduit.setItems(FXCollections.observableArrayList(items));
            
            if (selectedProduitId != null) {
                setSelectedProduitId(selectedProduitId);
            }
            
        } catch (Exception e) {
            showError("Erreur chargement produits : " + e.getMessage());
        }
    }

    private void afficherStockActuel(UUID produitId) {
        try {
            Stock stock = apiGetStockByProduit(produitId);
            if (stock != null) {
                lblStockActuel.setText("Stock actuel : " + stock.getQuantiteDisponible() + " unités");
                lblStockActuel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
            }
        } catch (Exception e) {
            lblStockActuel.setText("Stock actuel : N/A");
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (!validerFormulaire()) {
            return;
        }

        try {
            ProduitItem produit = cbProduit.getValue();
            int quantite = Integer.parseInt(txtQuantite.getText().trim());

            EntreeStock entree = new EntreeStock();
            entree.setProduitId(produit.getId());
            entree.setQuantite(quantite);
            
            // Optionnel : Commande Fournisseur
            String cmdRef = txtCommandeFournisseur.getText().trim();
            if (!cmdRef.isEmpty()) {
                // Si vous avez une API pour récupérer l'ID par référence, utilisez-la
                // Sinon, laissez null pour l'instant
                entree.setCommandeFournisseurId(null);
            }

            EntreeStock result = apiEnregistrerEntree(entree);
            
            showInfo("Entrée enregistrée avec succès !\n" +
                    "Produit : " + produit.getNom() + "\n" +
                    "Quantité : " + quantite);
            
            if (onSuccessCallback != null) {
                onSuccessCallback.accept(null);
            }
            
            fermerDialog();
            
        } catch (Exception e) {
            showError("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerDialog();
    }

    private boolean validerFormulaire() {
        if (cbProduit.getValue() == null) {
            showWarning("Veuillez sélectionner un produit");
            return false;
        }

        String qteText = txtQuantite.getText().trim();
        if (qteText.isEmpty()) {
            showWarning("Veuillez saisir une quantité");
            return false;
        }

        try {
            int qte = Integer.parseInt(qteText);
            if (qte <= 0) {
                showWarning("La quantité doit être supérieure à 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Quantité invalide");
            return false;
        }

        return true;
    }

    private void fermerDialog() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    // ==================== API CALLS ====================

    private List<Stock> apiGetAllStock() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), new TypeReference<List<Stock>>() {});
    }

    private Stock apiGetStockByProduit(UUID produitId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + produitId))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), Stock.class);
    }

    private EntreeStock apiEnregistrerEntree(EntreeStock entree) throws Exception {
        String json = mapper.writeValueAsString(entree);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/entrees"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode() + " - " + res.body());
        return mapper.readValue(res.body(), EntreeStock.class);
    }

    private String getAuthToken() {
        return Session.get().getToken();
    }

    // ==================== UTILITAIRES ====================

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    // ==================== CLASSE INTERNE ====================

    public static class ProduitItem {
        private UUID id;
        private String nom;
        private Integer stockActuel;

        public ProduitItem(UUID id, String nom, Integer stockActuel) {
            this.id = id;
            this.nom = nom;
            this.stockActuel = stockActuel;
        }

        public UUID getId() { return id; }
        public String getNom() { return nom; }
        public Integer getStockActuel() { return stockActuel; }

        @Override
        public String toString() {
            return nom + " (Stock: " + stockActuel + ")";
        }
    }
}