package gestiondestock.dao;

import com.google.gson.Gson;
import gestiondestock.model.ClientModel;
import gestiondestock.model.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ClientDAO {
    private static final String BASE_URL = "http://localhost:8080/api/clients";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public static ClientModel getById(String id) throws IOException, InterruptedException {
        HttpRequest req = baseRequest(BASE_URL + "/" + id)
                .GET()
                .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 200)
            throw new IOException("HTTP " + resp.statusCode());

        // Parse the response wrapper
        var body = GSON.fromJson(resp.body(), java.util.Map.class);
        var data = (java.util.Map) body.get("data");
        return GSON.fromJson(GSON.toJson(data), ClientModel.class);
    }

    public static ClientModel update(String id, ClientModel client) throws IOException, InterruptedException {
        String json = GSON.toJson(client);

        HttpRequest req = baseRequest(BASE_URL + "/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 200)
            throw new IOException("HTTP " + resp.statusCode());

        var body = GSON.fromJson(resp.body(), java.util.Map.class);
        var data = (java.util.Map) body.get("data");
        return GSON.fromJson(GSON.toJson(data), ClientModel.class);
    }

    private static HttpRequest.Builder baseRequest(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        String token = Session.get().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }
}
