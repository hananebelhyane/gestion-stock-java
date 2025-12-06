package gestiondestock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddCommandeFournisseurController {

    @FXML
    private TextField fournisseurNomField;
    @FXML
    private TextField fournisseurPrenomField;
    @FXML
    private TextField quantiteField;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private Runnable onCommandeCreated;

    public void setOnCommandeCreated(Runnable callback) {
        this.onCommandeCreated = callback;
    }

    @FXML
    private void initialize() {
        // Enum côté backend (minuscule désormais): en_attente, livree, annulee
        statutCombo.getItems().setAll("en_attente", "livree", "annulee");
        statutCombo.getSelectionModel().select("en_attente");
    }

    @FXML
    private void handleCancel() {
        close();
    }

    @FXML
    private void handleSave() {
        // Validation
        if (fournisseurNomField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nom produit obligatoire").showAndWait();
            return;
        }

        try {
            // Créer le payload JSON pour CommandeFournisseur
            var payload = new java.util.HashMap<String, Object>();

            var produit = new java.util.HashMap<String, String>();
            produit.put("nom", fournisseurNomField.getText());
            produit.put("description", fournisseurPrenomField.getText());
            payload.put("produit", produit);

            payload.put("statut", statutCombo.getValue());

            var gson = new com.google.gson.Gson();
            var body = gson.toJson(payload);

            System.out.println("📤 Envoi fournisseur au backend: " + body);

            var url = new java.net.URL("http://localhost:8080/api/commandes/fournisseurs");
            var conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            try (var out = conn.getOutputStream()) {
                out.write(body.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📥 Response code: " + responseCode);

            if (responseCode == 201 || responseCode == 200) {
                System.out.println("✅ Commande fournisseur créée avec succès");
                if (onCommandeCreated != null) {
                    onCommandeCreated.run();
                }
                close();
            } else {
                try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()))) {
                    String errorMsg = br.lines().reduce("", (a, b) -> a + b);
                    System.err.println("❌ Erreur serveur: " + errorMsg);
                    new Alert(Alert.AlertType.ERROR, "Erreur serveur: " + responseCode + "\n" + errorMsg).showAndWait();
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Quantité doit être un nombre").showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'enregistrer: " + ex.getMessage()).showAndWait();
        }
    }

    private void close() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
