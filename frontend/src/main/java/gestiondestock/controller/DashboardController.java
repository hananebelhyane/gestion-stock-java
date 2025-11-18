package gestiondestock.controller;

import gestiondestock.model.DashboardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label newClients7d;
    @FXML private Label clientOrders7d;
    @FXML private Label pendingSupplierOrders;
    @FXML private Label outOfStock;

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
    }
}
