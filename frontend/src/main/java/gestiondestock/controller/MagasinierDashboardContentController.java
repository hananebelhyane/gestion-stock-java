package gestiondestock.controller;

import gestiondestock.service.MagasinierDashboardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MagasinierDashboardContentController {
    
    @FXML private Label totalProducts;
    @FXML private Label lowStockCount;
    @FXML private Label outOfStockCount;
    @FXML private Label todayMovements;
    @FXML private Label pendingAlerts;

    @FXML
    public void initialize() {
        System.out.println("🔄 Initialisation du contenu du dashboard magasinier");
        loadDashboardSummary();
    }

    /**
     * Charge les statistiques du dashboard magasinier
     */
    private void loadDashboardSummary() {
        MagasinierDashboardService.fetchSummaryAsync(summary -> {
            Platform.runLater(() -> {
                totalProducts.setText(String.valueOf(summary.totalProducts));
                lowStockCount.setText(String.valueOf(summary.lowStockCount));
                outOfStockCount.setText(String.valueOf(summary.outOfStockCount));
                todayMovements.setText(String.valueOf(summary.todayMovements));
                pendingAlerts.setText(String.valueOf(summary.pendingAlerts));
                System.out.println("✅ Statistiques chargées dans le contenu du dashboard");
            });
        }, error -> {
            Platform.runLater(() -> {
                totalProducts.setText("-");
                lowStockCount.setText("-");
                outOfStockCount.setText("-");
                todayMovements.setText("-");
                pendingAlerts.setText("-");
                System.err.println("❌ Erreur lors du chargement du dashboard: " + error);
            });
        });
    }
}