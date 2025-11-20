package gestiondestock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddCommandeClientController {

    @FXML
    private TextField clientNomField;
    @FXML
    private TextField clientPrenomField;
    @FXML
    private TextField seuilMaxField;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private Runnable onCommandeCreated; // callback pour rafraîchir la table

    public void setOnCommandeCreated(Runnable callback) {
        this.onCommandeCreated = callback;
    }

    @FXML
    private void initialize() {
        statutCombo.getItems().setAll("en_attente", "confirmee", "annulee");
        statutCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleCancel() {
        close();
    }

    @FXML
    private void handleSave() {
        // Validation
        if (clientNomField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nom client obligatoire").showAndWait();
            return;
        }

        try {
            // Créer le payload JSON correct pour le backend
            var payload = new java.util.HashMap<String, Object>();

            // Client complet
            var client = new java.util.HashMap<String, String>();
            client.put("nom", clientNomField.getText());
            client.put("prenom", clientPrenomField.getText());
            payload.put("client", client);

            // Autres champs
            String seuilMaxText = seuilMaxField.getText().trim();
            if (!seuilMaxText.isEmpty()) {
                payload.put("seuilMax", Integer.parseInt(seuilMaxText));
            }
            payload.put("statut", statutCombo.getValue());

            var gson = new com.google.gson.Gson();
            var body = gson.toJson(payload);

            System.out.println("📤 Envoi au backend: " + body);

            var url = new java.net.URL("http://localhost:8080/api/commandes/clients");
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
                System.out.println("✅ Commande créée avec succès");
                if (onCommandeCreated != null) {
                    onCommandeCreated.run();
                }
                close();
            } else {
                // Lire le message d'erreur du serveur
                try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()))) {
                    String errorMsg = br.lines().reduce("", (a, b) -> a + b);
                    System.err.println("❌ Erreur serveur: " + errorMsg);
                    new Alert(Alert.AlertType.ERROR, "Erreur serveur: " + responseCode + "\n" + errorMsg).showAndWait();
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Seuil max doit être un nombre").showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'enregistrer: " + ex.getMessage()).showAndWait();
        }
    }

    private void close() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
