package gestiondestock.dao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gestiondestock.model.Session;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class CartDAO {
    private static final String API_BASE = "http://localhost:8080/api";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Add product to cart with a specific quantity[cite: 5].
     */
    public static OrderResponse addPanierItem(String produitId, int quantite) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("produitId", produitId);
            body.addProperty("quantite", quantite);

            HttpRequest request = baseRequestBuilder(API_BASE + "/commandes/panier/items")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                    .build();

            // FIXED: Used BodyHandlers instead of ValueHandlers [cite: 5]
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? gson.fromJson(response.body(), OrderResponse.class) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OrderResponse getPanier() {
        try {
            HttpRequest request = baseRequestBuilder(API_BASE + "/commandes/panier").GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? gson.fromJson(response.body(), OrderResponse.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static OrderResponse removePanierItem(String produitId) {
        try {
            HttpRequest request = baseRequestBuilder(API_BASE + "/commandes/panier/items/" + produitId).DELETE()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? gson.fromJson(response.body(), OrderResponse.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static ConfirmationResponse confirmOrder(String commandeId) {
        try {
            HttpRequest request = baseRequestBuilder(API_BASE + "/commandes/" + commandeId + "/confirm")
                    .POST(HttpRequest.BodyPublishers.ofString("")).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 ? gson.fromJson(response.body(), ConfirmationResponse.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static HttpRequest.Builder baseRequestBuilder(String url) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + Session.get().getToken())
                .header("X-Client-Id", Session.get().getUserId());
    }

    public static class OrderResponse {
        public String id;
        public List<LigneResponse> lignesCommande;

        public static class LigneResponse {
            public String produitId, produitNom;
            public int quantite;
            public double prixUnitaire, montantTotal;
        }
    }

    public static class ConfirmationResponse {
        public OrderResponse commande_data;
        public FactureResponse facture_data;
    }

    public static class FactureResponse {
        public String id;
        public double montantTotal;
        public boolean estPayee;
    }
}