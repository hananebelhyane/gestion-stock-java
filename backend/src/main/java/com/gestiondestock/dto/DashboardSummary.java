package com.gestiondestock.dto;

public class DashboardSummary {
    public int newClients7d;
    public int clientOrders7d;
    public int pendingSupplierOrders;
    public int outOfStock;

    public DashboardSummary(int newClients7d, int clientOrders7d, int pendingSupplierOrders, int outOfStock) {
        this.newClients7d = newClients7d;
        this.clientOrders7d = clientOrders7d;
        this.pendingSupplierOrders = pendingSupplierOrders;
        this.outOfStock = outOfStock;
    }
}