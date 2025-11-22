package gestiondestock.model;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AuthService {

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String baseUrl;

    public AuthService() {
        this("http://localhost:8082");
    }

    public AuthService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public AuthResponse login(String username, String password) throws Exception {
        String json = "{\"username\":\"" + escape(username) + "\",\"password\":\"" + escape(password) + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            return gson.fromJson(resp.body(), AuthResponse.class);
        } else if (resp.statusCode() == 401) {
            throw new RuntimeException("Invalid credentials");
        } else {
            throw new RuntimeException("Login failed: HTTP " + resp.statusCode());
        }
    }

    public void register(String username, String password, String email) throws Exception {
        String json = "{\"username\":\"" + escape(username) + "\",\"motDePasse\":\"" + escape(password) + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/clients/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200 || resp.statusCode() == 201) {
            return; // success
        } else if (resp.statusCode() == 409) {
            throw new RuntimeException("Username already exists");
        } else {
            throw new RuntimeException("Register failed: HTTP " + resp.statusCode());
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
