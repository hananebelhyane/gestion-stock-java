package gestiondestock.service;

import com.google.gson.Gson;
import gestiondestock.model.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MagasinierDashboardService {
    
    private static final String BASE_URL = "http://localhost:8080/api/magasinier/dashboard";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    
    /**
     * Classe pour représenter le résumé du dashboard magasinier
     */
    public static class Summary {
        public int totalProducts;
        public int lowStockCount;
        public int outOfStockCount;
        public int todayMovements;
        public int pendingAlerts;
        
        @Override
        public String toString() {
            return "Summary{" +
                    "totalProducts=" + totalProducts +
                    ", lowStockCount=" + lowStockCount +
                    ", outOfStockCount=" + outOfStockCount +
                    ", todayMovements=" + todayMovements +
                    ", pendingAlerts=" + pendingAlerts +
                    '}';
        }
    }

    /**
     * Récupère le résumé du dashboard de manière asynchrone
     */
    public static void fetchSummaryAsync(java.util.function.Consumer<Summary> onSuccess,
                                         java.util.function.Consumer<String> onError) {
        new Thread(() -> {
            try {
                var session = Session.get();
                
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/summary"))
                        .header("Content-Type", "application/json")
                        .GET();
                
                // Ajouter le token d'authentification si disponible
                if (session.getToken() != null && !session.getToken().isEmpty()) {
                    requestBuilder.header("Authorization", "Bearer " + session.getToken());
                }
                
                HttpRequest request = requestBuilder.build();
                
                System.out.println("🔄 Envoi de la requête vers: " + BASE_URL + "/summary");
                
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                System.out.println("📥 Réponse reçue - Status: " + response.statusCode());
                System.out.println("📥 Body: " + response.body());
                
                if (response.statusCode() == 200) {
                    Summary summary = gson.fromJson(response.body(), Summary.class);
                    System.out.println("✅ Statistiques parsées: " + summary);
                    onSuccess.accept(summary);
                } else {
                    String error = "Erreur HTTP " + response.statusCode() + ": " + response.body();
                    System.err.println("❌ " + error);
                    onError.accept(error);
                }
                
            } catch (Exception ex) {
                String error = "Erreur lors de la récupération des statistiques: " + ex.getMessage();
                System.err.println("❌ " + error);
                ex.printStackTrace();
                onError.accept(error);
            }
        }).start();
    }
}