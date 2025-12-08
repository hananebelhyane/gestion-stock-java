package gestiondestock.service;

import com.google.gson.Gson;
import gestiondestock.model.MagasinierProfileModel;
import gestiondestock.model.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MagasinierProfileService {
    private static final String BASE_URL = "http://localhost:8080/api/magasiniers";
    private final Gson gson = new Gson();

    // Obtenir le token de la session
    private String getAuthToken() {
        String token = Session.get().getToken();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Non authentifié. Veuillez vous connecter.");
        }
        return token;
    }

    // Récupérer le profil du magasinier connecté
    public MagasinierProfileModel getMyProfile() throws Exception {
        URL url = new URL(BASE_URL + "/profile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé.");
        }
        if (responseCode != 200) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        // Parser la réponse qui contient {success, message, data}
        com.google.gson.JsonObject jsonResponse = gson.fromJson(response, com.google.gson.JsonObject.class);
        String dataJson = jsonResponse.get("data").toString();
        
        return gson.fromJson(dataJson, MagasinierProfileModel.class);
    }

    // Mettre à jour le profil
    public MagasinierProfileModel updateProfile(MagasinierProfileModel profile) throws Exception {
        URL url = new URL(BASE_URL + "/profile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());
        conn.setDoOutput(true);

        String jsonInput = gson.toJson(profile);
        try(OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé.");
        }
        if (responseCode != 200) {
            // Lire le message d'erreur
            try {
                String errorResponse = readErrorResponse(conn);
                com.google.gson.JsonObject errorJson = gson.fromJson(errorResponse, com.google.gson.JsonObject.class);
                String errorMessage = errorJson.get("message").getAsString();
                throw new Exception(errorMessage);
            } catch (Exception e) {
                throw new Exception("Erreur HTTP: " + responseCode);
            }
        }

        String response = readResponse(conn);
        
        // Parser la réponse qui contient {success, message, data}
        com.google.gson.JsonObject jsonResponse = gson.fromJson(response, com.google.gson.JsonObject.class);
        String dataJson = jsonResponse.get("data").toString();
        
        return gson.fromJson(dataJson, MagasinierProfileModel.class);
    }

    // Changer le mot de passe
    public void changePassword(String oldPassword, String newPassword) throws Exception {
        URL url = new URL(BASE_URL + "/change-password");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());
        conn.setDoOutput(true);

        String jsonInput = String.format("{\"oldPassword\":\"%s\",\"newPassword\":\"%s\"}", 
            escapeJson(oldPassword), escapeJson(newPassword));
        
        try(OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Ancien mot de passe incorrect");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé.");
        }
        if (responseCode == 400) {
            // Lire le message d'erreur
            try {
                String errorResponse = readErrorResponse(conn);
                com.google.gson.JsonObject errorJson = gson.fromJson(errorResponse, com.google.gson.JsonObject.class);
                String errorMessage = errorJson.get("message").getAsString();
                throw new Exception(errorMessage);
            } catch (Exception e) {
                throw new Exception("Erreur lors du changement de mot de passe");
            }
        }
        if (responseCode != 200 && responseCode != 204) {
            throw new Exception("Erreur lors du changement de mot de passe");
        }
    }

    // Méthode utilitaire pour lire la réponse
    private String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return response.toString();
    }

    // Échapper les caractères spéciaux dans JSON
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    // Lire la réponse d'erreur
    private String readErrorResponse(HttpURLConnection conn) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return response.toString();
    }
}