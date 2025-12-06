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
    private void handleSave(javafx.event.ActionEvent event) {
        // Validation
        if (clientNomField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nom client obligatoire").showAndWait();
            return;
        }

        if (clientPrenomField.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Prénom client obligatoire").showAndWait();
            return;
        }

        // Vérifier que le statut est sélectionné
        String statut = statutCombo.getValue();
        if (statut == null || statut.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un statut").showAndWait();
            return;
        }

        try {
            // Créer le payload JSON correct pour le backend
            var payload = new java.util.HashMap<String, Object>();

            // Client complet
            var client = new java.util.HashMap<String, String>();
            client.put("nom", clientNomField.getText().trim());
            client.put("prenom", clientPrenomField.getText().trim());
            payload.put("client", client);

            // Autres champs
            String seuilMaxText = seuilMaxField.getText().trim();
            if (!seuilMaxText.isEmpty()) {
                try {
                    payload.put("seuilMax", Integer.parseInt(seuilMaxText));
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Seuil max doit être un nombre valide").showAndWait();
                    return;
                }
            }
            payload.put("statut", statut);

            var gson = new com.google.gson.Gson();
            var body = gson.toJson(payload);

            System.out.println("📤 Envoi au backend: " + body);

            var url = new java.net.URL("http://localhost:8080/api/commandes/clients");
            var conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            // Add authorization header
            String token = gestiondestock.model.Session.get().getToken();
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

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
                String errorMsg = "Erreur serveur: " + responseCode;
                try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()))) {
                    String serverMsg = br.lines().reduce("", (a, b) -> a + b);
                    if (serverMsg != null && !serverMsg.isEmpty()) {
                        errorMsg += "\n" + serverMsg;
                    }
                } catch (Exception e) {
                    // Ignore si on ne peut pas lire le stream
                }
                System.err.println("❌ " + errorMsg);
                new Alert(Alert.AlertType.ERROR, errorMsg).showAndWait();
            }
        } catch (java.net.ConnectException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible de se connecter au serveur. Vérifiez que le backend est démarré.").showAndWait();
        } catch (java.net.SocketTimeoutException e) {
            new Alert(Alert.AlertType.ERROR, "Timeout: Le serveur ne répond pas.").showAndWait();
        } catch (java.io.IOException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "Erreur de communication avec le serveur";
            }
            new Alert(Alert.AlertType.ERROR, "Erreur: " + errorMsg).showAndWait();
            e.printStackTrace();
        } catch (Exception ex) {
            String errorMsg = ex.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = ex.getClass().getSimpleName();
            }
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'enregistrer: " + errorMsg).showAndWait();
        }
    }

    private void close() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
