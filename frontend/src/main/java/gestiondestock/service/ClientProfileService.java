package gestiondestock.service;

import com.google.gson.Gson;
import gestiondestock.model.ClientProfileModel;
import gestiondestock.model.Session;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClientProfileService {

    private static final String BASE_URL = "http://localhost:8080/api/clients";
    private final Gson gson = new Gson();

    private String getAuthToken() {
        String token = Session.get().getToken();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Non authentifié");
        }
        return token;
    }

    public ClientProfileModel getMyProfile() throws Exception {
        URL url = new URL(BASE_URL + "/profile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new Exception("Erreur récupération profil client");
        }

        String response = read(conn);
        var json = gson.fromJson(response, com.google.gson.JsonObject.class);
        return gson.fromJson(json.get("data"), ClientProfileModel.class);
    }

    public ClientProfileModel updateProfile(ClientProfileModel profile) throws Exception {
        URL url = new URL(BASE_URL + "/profile");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(profile).getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() != 200) {
            throw new Exception("Erreur mise à jour profil");
        }

        String response = read(conn);
        var json = gson.fromJson(response, com.google.gson.JsonObject.class);
        return gson.fromJson(json.get("data"), ClientProfileModel.class);
    }

    public void changePassword(String oldPwd, String newPwd) throws Exception {
        URL url = new URL(BASE_URL + "/change-password");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + getAuthToken());
        conn.setRequestProperty("Content-Type", "application/json");

        String body = String.format(
                "{\"oldPassword\":\"%s\",\"newPassword\":\"%s\"}", oldPwd, newPwd);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();

        if (code != 200 && code != 204) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            throw new Exception("Erreur changement mot de passe : " + sb.toString());
        }

    }

    private String read(HttpURLConnection conn) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        return sb.toString();
    }
}
