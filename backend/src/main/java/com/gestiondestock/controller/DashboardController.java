package com.gestiondestock.controller;

import com.gestiondestock.dto.DashboardSummary;
import com.gestiondestock.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
