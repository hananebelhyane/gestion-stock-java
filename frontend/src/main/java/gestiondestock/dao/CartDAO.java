package gestiondestock.dao;

import gestiondestock.model.CartItem;
import gestiondestock.model.Session;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CartDAO {

    private static final String API_BASE = "http://localhost:8080/api";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Send cart items to backend and create/update commande
     */
    public static OrderResponse checkout(List<CartItem> cartItems) {
        try {
            // Convert CartItems to LigneCommandeDTO format
            JsonArray lignesJson = new JsonArray();
            for (CartItem item : cartItems) {
                JsonObject ligne = new JsonObject();
                ligne.addProperty("produitId", item.getProduit().getId());
                ligne.addProperty("quantite", item.getQuantite());
                ligne.addProperty("prixUnitaire", item.getProduit().getPrixUnitaire());
                lignesJson.add(ligne);
            }

            String requestBody = gson.toJson(lignesJson);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/checkout"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                OrderResponse orderResponse = gson.fromJson(response.body(), OrderResponse.class);
                return orderResponse;
            } else {
                System.err.println("Checkout failed: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Persist a cart delta immediately.
     * POST /api/commandes/panier/items
     * Body: { produitId: <uuid>, quantite: <delta> }
     */
    public static OrderResponse addPanierItem(String produitId, int quantiteDelta) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("produitId", produitId);
            body.addProperty("quantite", quantiteDelta);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/panier/items"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), OrderResponse.class);
            }
            System.err.println("Add panier item failed: " + response.statusCode());
            System.err.println("Response: " + response.body());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Remove a product line from pending cart.
     * DELETE /api/commandes/panier/items/{produitId}
     */
    public static OrderResponse removePanierItem(String produitId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/panier/items/" + produitId))
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), OrderResponse.class);
            }
            System.err.println("Remove panier item failed: " + response.statusCode());
            System.err.println("Response: " + response.body());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get current pending cart for client.
     * GET /api/commandes/panier
     */
    public static OrderResponse getPanier() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/panier"))
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), OrderResponse.class);
            } else if (response.statusCode() == 404) {
                return null;
            }
            System.err.println("Failed to get panier: " + response.statusCode());
            System.err.println("Response: " + response.body());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get order history for the connected client.
     * GET /api/commandes/client/history
     */
    public static List<OrderResponse> getOrderHistory() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/client/history"))
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                OrderResponse[] arr = gson.fromJson(response.body(), OrderResponse[].class);
                if (arr == null) {
                    return List.of();
                }
                return Arrays.asList(arr);
            }
            System.err.println("Failed to get order history: " + response.statusCode());
            System.err.println("Response: " + response.body());
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Confirm order and generate invoice
     */
    public static ConfirmationResponse confirmOrder(String commandeId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/" + commandeId + "/confirm"))
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ConfirmationResponse confirmResponse = gson.fromJson(response.body(), ConfirmationResponse.class);
                return confirmResponse;
            } else {
                System.err.println("Confirmation failed: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get current pending cart for client
     */
    public static OrderResponse getPendingCart() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/pending"))
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), OrderResponse.class);
            } else if (response.statusCode() == 404) {
                return null; // No pending cart
            } else {
                System.err.println("Failed to get pending cart: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get facture (invoice) for an order
     */
    public static FactureResponse getFacture(String commandeId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commandes/" + commandeId + "/facture"))
                    .header("Authorization", "Bearer " + Session.get().getToken())
                    .header("X-Client-Id", Session.get().getUserId())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), FactureResponse.class);
            } else {
                System.err.println("Failed to get facture: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Response DTOs
    public static class OrderResponse {
        public String id;
        public String clientId;
        public String dateCommande;
        public String statut;
        public double montantTotal;
        public List<LigneResponse> lignesCommande;

        public static class LigneResponse {
            public String id;
            public String produitId;
            public String produitNom;
            public int quantite;
            public double prixUnitaire;
            public double montantTotal;
        }
    }

    public static class ConfirmationResponse {
        public OrderResponse commande_data;
        public FactureResponse facture_data;
    }

    public static class FactureResponse {
        public String id;
        public String commandeId;
        public String clientId;
        public String dateFacture;
        public double montantTotal;
        public boolean estPayee;
    }
}
