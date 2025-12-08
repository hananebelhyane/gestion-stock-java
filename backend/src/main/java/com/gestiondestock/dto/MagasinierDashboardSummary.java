package com.gestiondestock.dto;

public class MagasinierDashboardSummary {
    public int totalProducts;
    public int lowStockCount;
    public int outOfStockCount;
    public int todayMovements;
    public int pendingAlerts;

    public MagasinierDashboardSummary(int totalProducts, int lowStockCount, 
                                      int outOfStockCount, int todayMovements, 
                                      int pendingAlerts) {
        this.totalProducts = totalProducts;
        this.lowStockCount = lowStockCount;
        this.outOfStockCount = outOfStockCount;
        this.todayMovements = todayMovements;
        this.pendingAlerts = pendingAlerts;
    }
}