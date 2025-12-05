package gestiondestock.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gestiondestock.model.ClientModel;
import gestiondestock.model.Session;

import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import gestiondestock.util.LocalDateTimeAdapter;
import gestiondestock.util.UUIDAdapter;

public class ClientService {
    private static final String BASE_URL = "http://localhost:8080/api/clients";
    private final Gson gson;

    public ClientService() {
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(UUID.class, new UUIDAdapter())
            .create();
    }

    // ✅ Méthode pour obtenir le token de la session
    private String getAuthToken() {
        String token = Session.get().getToken();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Non authentifié. Veuillez vous connecter.");
        }
        return token;
    }

    // ✅ Vérifier si l'utilisateur est admin
    private void checkAdminRole() {
        String role = Session.get().getRole();
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Accès refusé. Seuls les administrateurs peuvent gérer les clients.");
        }
    }

    // Récupérer tous les clients actifs
    public List<ClientModel> getAllActiveClients() throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        Map<String, Object> result = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        String dataJson = gson.toJson(result.get("data"));
        return gson.fromJson(dataJson, new TypeToken<List<ClientModel>>(){}.getType());
    }

    // Créer un client
    public ClientModel createClient(ClientModel client) throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token
        conn.setDoOutput(true);

        // Créer un objet avec motDePasse au lieu de mot_de_passe
        Map<String, String> requestBody = Map.of(
            "nom", client.getNom(),
            "prenom", client.getPrenom(),
            "username", client.getUsername(),
            "telephone", client.getTelephone(),
            "motDePasse", "default123", // Mot de passe par défaut
            "adresse", client.getAdresse()
        );
        
        String jsonInput = gson.toJson(requestBody);
        try(OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200 && responseCode != 201) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        Map<String, Object> result = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        String dataJson = gson.toJson(result.get("data"));
        return gson.fromJson(dataJson, ClientModel.class);
    }

    // Mettre à jour un client
    public ClientModel updateClient(UUID id, ClientModel client) throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL + "/" + id.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token
        conn.setDoOutput(true);

        Map<String, String> requestBody = Map.of(
            "nom", client.getNom(),
            "prenom", client.getPrenom(),
            "username", client.getUsername(),
            "telephone", client.getTelephone(),
            "motDePasse", "default123",
            "adresse", client.getAdresse()
        );

        String jsonInput = gson.toJson(requestBody);
        try(OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        Map<String, Object> result = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        String dataJson = gson.toJson(result.get("data"));
        return gson.fromJson(dataJson, ClientModel.class);
    }

    // Supprimer un client
    public void deleteClient(UUID id) throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL + "/" + id.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200 && responseCode != 204) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }
    }

    // Rechercher des clients
    public List<ClientModel> searchClients(String keyword) throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL + "/search?keyword=" + keyword);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        Map<String, Object> result = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        String dataJson = gson.toJson(result.get("data"));
        return gson.fromJson(dataJson, new TypeToken<List<ClientModel>>(){}.getType());
    }

    // Récupérer un client par ID
    public ClientModel getClientById(UUID id) throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL + "/" + id.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        Map<String, Object> result = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        String dataJson = gson.toJson(result.get("data"));
        return gson.fromJson(dataJson, ClientModel.class);
    }
    
    // Récupérer les clients supprimés
    public List<ClientModel> getDeletedClients() throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        URL url = new URL(BASE_URL + "/deleted");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token

        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new Exception("Session expirée. Veuillez vous reconnecter.");
        }
        if (responseCode == 403) {
            throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
        }
        if (responseCode != 200) {
            throw new Exception("Erreur HTTP: " + responseCode);
        }

        String response = readResponse(conn);
        
        Map<String, Object> result = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        String dataJson = gson.toJson(result.get("data"));
        return gson.fromJson(dataJson, new TypeToken<List<ClientModel>>(){}.getType());
    }
    
    // ✅ RESTAURER UN CLIENT avec authentification
    public void restoreClient(UUID id) throws Exception {
        checkAdminRole(); // ✅ Vérification du rôle
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        
        try {
            String urlStr = BASE_URL + "/" + id.toString() + "/restore";
            HttpPatch httpPatch = new HttpPatch(urlStr);
            httpPatch.setHeader("Content-Type", "application/json");
            httpPatch.setHeader("Accept", "application/json");
            httpPatch.setHeader("Authorization", "Bearer " + getAuthToken()); // ✅ Ajout du token
            
            response = httpClient.execute(httpPatch);
            int statusCode = response.getCode();
            
            if (statusCode == 401) {
                throw new Exception("Session expirée. Veuillez vous reconnecter.");
            }
            if (statusCode == 403) {
                throw new Exception("Accès refusé. Vous n'avez pas les permissions nécessaires.");
            }
            if (statusCode != 200 && statusCode != 204) {
                String errorBody = "";
                try {
                    if (response.getEntity() != null) {
                        errorBody = EntityUtils.toString(response.getEntity());
                    }
                } catch (Exception e) {
                    // Ignorer si impossible de lire le body
                }
                throw new Exception("Erreur HTTP: " + statusCode + 
                    (errorBody.isEmpty() ? "" : " - " + errorBody));
            }
            
        } catch (Exception e) {
            throw new Exception("Erreur lors de la restauration: " + e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    // Ignorer
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // Ignorer
                }
            }
        }
    }

    // Méthode utilitaire pour lire la réponse complète
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
}