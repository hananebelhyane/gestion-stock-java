package gestiondestock.controller;

import gestiondestock.model.DashboardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;

public class DashboardController {
    @FXML private Label newClients7d;
    @FXML private Label clientOrders7d;
    @FXML private Label pendingSupplierOrders;
    @FXML private Label outOfStock;

    @FXML private LineChart<String, Number> ordersLineChart;
    @FXML private ComboBox<String> rangeSelector;

    @FXML
    private VBox activitiesList;
    @FXML
    private VBox alertsList;

    @FXML
    public void initialize() {
        DashboardService.fetchSummaryAsync(sum -> {
            Platform.runLater(() -> {
                newClients7d.setText(String.valueOf(sum.newClients7d));
                clientOrders7d.setText(String.valueOf(sum.clientOrders7d));
                pendingSupplierOrders.setText(String.valueOf(sum.pendingSupplierOrders));
                outOfStock.setText(String.valueOf(sum.outOfStock));
            });
        }, err -> {
            Platform.runLater(() -> {
                newClients7d.setText("-");
                clientOrders7d.setText("-");
                pendingSupplierOrders.setText("-");
                outOfStock.setText("-");
            });
        });

        // Initialize range selector and load chart
        if (rangeSelector != null && rangeSelector.getItems().isEmpty()) {
            rangeSelector.getItems().setAll("This month", "Last 7 days");
            rangeSelector.getSelectionModel().select("This month");
        }
        loadOrdersChartForRange(rangeSelector != null ? rangeSelector.getSelectionModel().getSelectedItem() : "This month");

        // Apply chart styling and load lists
        initOrdersChartStyling();
        loadActivitiesAndAlerts();
    }

    private void initOrdersChartStyling() {
        // Increase axis label font sizes via CSS class already set on chart
        // Hide legend if single series
        ordersLineChart.setLegendVisible(false);
        ordersLineChart.setCreateSymbols(true);
        // Ensure brand series color via CSS class 'brand-blue' set in FXML
        ordersLineChart.setAnimated(true);
        ordersLineChart.setAlternativeRowFillVisible(false);
        ordersLineChart.setAlternativeColumnFillVisible(false);
    }

    private void loadActivitiesAndAlerts() {
        DashboardService.fetchRecentActivitiesAsync(10, items -> {
            Platform.runLater(() -> {
                activitiesList.getChildren().clear();
                if (items == null || items.length == 0) {
                    activitiesList.getChildren().add(new Label("No recent activities"));
                    return;
                }
                for (DashboardService.ActivityItem it : items) {
                    HBox row = new HBox(12);
                    row.getStyleClass().add("item");
                    Label date = new Label(formatDate(it.date));
                    date.getStyleClass().add("date");
                    Label qtyProd = new Label((it.type != null && it.type.equals("ENTRY") ? "+" : "-") + it.quantity + " " + safe(it.product));
                    qtyProd.getStyleClass().add("title");
                    Label type = new Label(it.type != null ? it.type : "");
                    type.getStyleClass().addAll("badge", (it.type != null && it.type.equals("ENTRY")) ? "entry" : "removal");
                    row.getChildren().addAll(date, qtyProd, type);
                    activitiesList.getChildren().add(row);
                }
            });
        }, err -> {
            Platform.runLater(() -> {
                activitiesList.getChildren().clear();
                activitiesList.getChildren().add(new Label("Unable to load activities"));
            });
        });

        DashboardService.fetchRecentAlertsAsync(10, items -> {
            Platform.runLater(() -> {
                alertsList.getChildren().clear();
                if (items == null || items.length == 0) {
                    alertsList.getChildren().add(new Label("No recent alerts"));
                    return;
                }
                for (DashboardService.AlertItem it : items) {
                    HBox row = new HBox(12);
                    row.getStyleClass().add("item");
                    Label date = new Label(formatDate(it.date));
                    date.getStyleClass().add("date");
                    Label msg = new Label((it.message != null && !it.message.isBlank()) ? it.message : ("Alert: " + safe(it.product)));
                    msg.getStyleClass().add("title");
                    row.getChildren().addAll(date, msg);
                    alertsList.getChildren().add(row);
                }
            });
        }, err -> {
            Platform.runLater(() -> {
                alertsList.getChildren().clear();
                alertsList.getChildren().add(new Label("Unable to load alerts"));
            });
        });
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String formatDate(String iso) {
        try {
            java.time.OffsetDateTime dt = java.time.OffsetDateTime.parse(iso);
            return dt.toLocalDate().toString();
        } catch (Exception ex) {
            try {
                java.time.LocalDateTime dt = java.time.LocalDateTime.parse(iso);
                return dt.toLocalDate().toString();
            } catch (Exception e2) {
                return iso;
            }
        }
    }

    @FXML
    public void onRangeChanged(ActionEvent e) {
        String sel = (rangeSelector != null && rangeSelector.getSelectionModel().getSelectedItem() != null)
                ? rangeSelector.getSelectionModel().getSelectedItem()
                : "This month";
        loadOrdersChartForRange(sel);
    }

    private void loadOrdersChartForRange(String range) {
        int days;
        if ("Last 7 days".equals(range)) {
            days = 7;
        } else {
            days = java.time.LocalDate.now().getDayOfMonth();
        }
        DashboardService.fetchOrdersOverTimeAsync(days, points -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Orders");
            for (var p : points) {
                String day = extractDay(p.date);
                series.getData().add(new XYChart.Data<>(day, p.count));
            }
            Platform.runLater(() -> {
                ordersLineChart.getData().clear();
                ordersLineChart.getData().add(series);
                ordersLineChart.setLegendVisible(ordersLineChart.getData().size() > 1);
            });
        }, err -> {
            Platform.runLater(() -> {
                ordersLineChart.getData().clear();
            });
        });
    }

    private String extractDay(String iso) {
        try {
            return String.valueOf(java.time.LocalDate.parse(iso).getDayOfMonth());
        } catch (Exception ex) {
            try {
                return String.valueOf(java.time.OffsetDateTime.parse(iso).getDayOfMonth());
            } catch (Exception e2) {
                try {
                    return String.valueOf(java.time.LocalDateTime.parse(iso).getDayOfMonth());
                } catch (Exception e3) {
                    // Fallback: try last token after '-'
                    int idx = iso.lastIndexOf('-');
                    if (idx != -1 && idx + 1 < iso.length()) {
                        String tail = iso.substring(idx + 1).replaceAll("[^0-9]", "");
                        if (!tail.isEmpty()) return tail;
                    }
                    return iso;
                }
            }
        }
    }
}
