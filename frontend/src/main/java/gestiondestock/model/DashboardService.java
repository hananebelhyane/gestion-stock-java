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

    public static class OrdersPoint {
        public String date; // ISO date string
        public int count;
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

    public static void fetchOrdersOverTimeAsync(int days,
                                                java.util.function.Consumer<OrdersPoint[]> onOk,
                                                java.util.function.Consumer<String> onErr) {
        try {
            var s = Session.get();
            String base = "http://localhost:8080";
            HttpRequest.Builder b = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/dashboard/orders-over-time?days=" + days))
                    .GET();
            if (s.getToken() != null && !s.getToken().isEmpty()) {
                b.header("Authorization", "Bearer " + s.getToken());
            }
            HttpRequest req = b.build();
            HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> {
                        try {
                            OrdersPoint[] points = new Gson().fromJson(body, OrdersPoint[].class);
                            onOk.accept(points);
                        } catch (Exception ex) {
                            onErr.accept("parse");
                        }
                    })
                    .exceptionally(ex -> { onErr.accept("http"); return null; });
        } catch (Exception ex) {
            onErr.accept("init");
        }
    }

    public static class ActivityItem {
        public String product;
        public int quantity;
        public String date; // ISO date-time
        public String type; // ENTRY or REMOVAL
    }

    public static class AlertItem {
        public String product;
        public String date; // ISO date-time
        public String message;
    }

    public static void fetchRecentActivitiesAsync(int limit,
                                                  java.util.function.Consumer<ActivityItem[]> onOk,
                                                  java.util.function.Consumer<String> onErr) {
        try {
            var s = Session.get();
            String base = "http://localhost:8080";
            HttpRequest.Builder b = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/dashboard/recent-activities?limit=" + limit))
                    .GET();
            if (s.getToken() != null && !s.getToken().isEmpty()) {
                b.header("Authorization", "Bearer " + s.getToken());
            }
            HttpRequest req = b.build();
            HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> {
                        try {
                            ActivityItem[] items = new Gson().fromJson(body, ActivityItem[].class);
                            onOk.accept(items);
                        } catch (Exception ex) {
                            onErr.accept("parse");
                        }
                    })
                    .exceptionally(ex -> { onErr.accept("http"); return null; });
        } catch (Exception ex) {
            onErr.accept("init");
        }
    }

    public static void fetchRecentAlertsAsync(int limit,
                                              java.util.function.Consumer<AlertItem[]> onOk,
                                              java.util.function.Consumer<String> onErr) {
        try {
            var s = Session.get();
            String base = "http://localhost:8080";
            HttpRequest.Builder b = HttpRequest.newBuilder()
                    .uri(URI.create(base + "/api/dashboard/recent-alerts?limit=" + limit))
                    .GET();
            if (s.getToken() != null && !s.getToken().isEmpty()) {
                b.header("Authorization", "Bearer " + s.getToken());
            }
            HttpRequest req = b.build();
            HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> {
                        try {
                            AlertItem[] items = new Gson().fromJson(body, AlertItem[].class);
                            onOk.accept(items);
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
