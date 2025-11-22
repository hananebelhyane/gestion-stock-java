package com.gestiondestock.controller;

import com.gestiondestock.dto.DashboardSummary;
import com.gestiondestock.dto.OrdersOverTimePoint;
import com.gestiondestock.dto.RecentActivity;
import com.gestiondestock.dto.StockAlertItem;
import com.gestiondestock.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return new DashboardSummary(
                service.newClients7d(),
                service.clientOrders7d(),
                service.pendingSupplierOrders(),
                service.outOfStock()
        );
    }

    @GetMapping("/orders-over-time")
    public List<OrdersOverTimePoint> ordersOverTime(@RequestParam(name = "days", defaultValue = "30") int days) {
        return service.ordersOverTime(days);
    }

    @GetMapping("/recent-activities")
    public List<RecentActivity> recentActivities(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        return service.recentActivities(limit);
    }

    @GetMapping("/recent-alerts")
    public List<StockAlertItem> recentAlerts(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        return service.recentAlerts(limit);
    }
}
