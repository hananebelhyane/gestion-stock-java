package gestiondestock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestiondestock.model.Session;
import gestiondestock.model.SortieStock;
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

public class SortieStockDialogController {

    private static final String BASE_URL = "http://localhost:8080/api/stock";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML private ComboBox<ProduitItem> cbProduit;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtLigneCommande;
    @FXML private TextArea txtNotes;
    @FXML private Button btnEnregistrer;
    @FXML private Button btnAnnuler;
    @FXML private Label lblStockActuel;
    @FXML private Label lblWarning;

    private Consumer<Void> onSuccessCallback;
    private UUID selectedProduitId;

    @FXML
    public void initialize() {
        chargerProduits();
        
        cbProduit.setOnAction(e -> {
            ProduitItem selected = cbProduit.getValue();
            if (selected != null) {
                afficherStockActuel(selected.getId(), selected.getStockActuel());
            }
        });

        txtQuantite.textProperty().addListener((obs, old, newVal) -> {
            verifierQuantite();
        });
    }

    public void setSelectedProduitId(UUID produitId) {
        this.selectedProduitId = produitId;
        if (cbProduit.getItems() != null && !cbProduit.getItems().isEmpty()) {
            for (ProduitItem item : cbProduit.getItems()) {
                if (item.getId().equals(produitId)) {
                    cbProduit.setValue(item);
                    afficherStockActuel(produitId, item.getStockActuel());
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
                .filter(s -> s.getProduit() != null && s.getQuantiteDisponible() > 0)
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

    private void afficherStockActuel(UUID produitId, Integer stockActuel) {
        lblStockActuel.setText("Stock actuel : " + stockActuel + " unités");
        lblStockActuel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        verifierQuantite();
    }

    private void verifierQuantite() {
        ProduitItem selected = cbProduit.getValue();
        String qteText = txtQuantite.getText().trim();
        
        if (selected == null || qteText.isEmpty()) {
            lblWarning.setVisible(false);
            return;
        }

        try {
            int qte = Integer.parseInt(qteText);
            if (qte > selected.getStockActuel()) {
                lblWarning.setText("⚠️ Quantité supérieure au stock disponible !");
                lblWarning.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                lblWarning.setVisible(true);
            } else {
                lblWarning.setVisible(false);
            }
        } catch (NumberFormatException e) {
            lblWarning.setVisible(false);
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

            SortieStock sortie = new SortieStock();
            sortie.setProduitId(produit.getId());
            sortie.setQuantite(quantite);
            
            // Optionnel : Ligne Commande
            String ligneRef = txtLigneCommande.getText().trim();
            if (!ligneRef.isEmpty()) {
                // Si vous avez une API pour récupérer l'ID par référence, utilisez-la
                // Sinon, laissez null pour l'instant
                sortie.setLigneCommandeId(null);
            }

            SortieStock result = apiEnregistrerSortie(sortie);
            
            showInfo("Sortie enregistrée avec succès !\n" +
                    "Produit : " + produit.getNom() + "\n" +
                    "Quantité : " + quantite + "\n" +
                    "Nouveau stock : " + (produit.getStockActuel() - quantite));
            
            if (onSuccessCallback != null) {
                onSuccessCallback.accept(null);
            }
            
            fermerDialog();
            
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("Stock insuffisant")) {
                showError("Stock insuffisant !\n\n" + errorMsg);
            } else {
                showError("Erreur lors de l'enregistrement : " + errorMsg);
            }
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

        ProduitItem produit = cbProduit.getValue();
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
            
            if (qte > produit.getStockActuel()) {
                showWarning("Stock insuffisant !\n\n" +
                          "Quantité demandée : " + qte + "\n" +
                          "Stock disponible : " + produit.getStockActuel());
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

    private SortieStock apiEnregistrerSortie(SortieStock sortie) throws Exception {
        String json = mapper.writeValueAsString(sortie);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sorties"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        // Gestion spéciale du code 409 (CONFLICT) pour stock insuffisant
        if (res.statusCode() == 409) {
            throw new Exception(res.body());
        }
        
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode() + " - " + res.body());
            
        return mapper.readValue(res.body(), SortieStock.class);
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