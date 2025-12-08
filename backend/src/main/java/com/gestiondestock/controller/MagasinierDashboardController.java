package com.gestiondestock.controller;

import com.gestiondestock.dto.MagasinierDashboardSummary;
import com.gestiondestock.service.MagasinierDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/magasinier/dashboard")
public class MagasinierDashboardController {
    
    private final MagasinierDashboardService service;
    
    public MagasinierDashboardController(MagasinierDashboardService service) {
        this.service = service;
    }

    /**
     * Récupère le résumé du dashboard magasinier
     */
    @GetMapping("/summary")
    public MagasinierDashboardSummary getSummary(Authentication authentication) {
        String username = authentication.getName();
        return service.getDashboardSummary(username);
    }
}