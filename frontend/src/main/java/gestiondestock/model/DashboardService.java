package gestiondestock.model;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardService {
    public static class Summary {
        public int newClients7d;
        public int clientOrders7d;
        public int pendingSupplierOrders;
        public int outOfStock;
    }

    public static void fetchSummaryAsync(java.util.function.Consumer<Summary> onOk,
                                         java.util.function.Consumer<String> onErr) {
        try {
            var s = Session.get();
            String base = "http://localhost:8080"; // align with AuthService default
            HttpRequest.Builder b = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/dashboard/summary"))
                    .GET();
            if (s.getToken() != null && !s.getToken().isEmpty()) {
                b.header("Authorization", "Bearer " + s.getToken());
            }
            HttpRequest req = b.build();
            HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> {
                        try {
                            Summary sum = new Gson().fromJson(body, Summary.class);
                            onOk.accept(sum);
                        } catch (Exception ex) {
                            onErr.accept("parse");
                        }
                    })
                    .exceptionally(ex -> { onErr.accept("http"); return null; });
        } catch (Exception ex) {
            onErr.accept("init");
        }
    }
}
