package com.gestiondestock.service;

import com.gestiondestock.dto.MagasinierDashboardSummary;
import com.gestiondestock.repository.StockRepository;
import com.gestiondestock.repository.EntreeStockRepository;
import com.gestiondestock.repository.SortieStockRepository;
import com.gestiondestock.repository.AlerteStockRepository;
import com.gestiondestock.repository.ProduitRepository;
import com.gestiondestock.entity.AlerteStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MagasinierDashboardService {
    
    @Autowired
    private ProduitRepository produitRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private EntreeStockRepository entreeStockRepository;
    
    @Autowired
    private SortieStockRepository sortieStockRepository;
    
    @Autowired
    private AlerteStockRepository alerteStockRepository;

    /**
     * Récupère le résumé du dashboard magasinier
     */
    public MagasinierDashboardSummary getDashboardSummary(String username) {
        int totalProducts = getTotalProducts();
        int lowStockCount = getLowStockCount();
        int outOfStockCount = getOutOfStockCount();
        int todayMovements = getTodayStockMovements();
        int pendingAlerts = getPendingAlerts();
        
        System.out.println("📊 Statistiques calculées:");
        System.out.println("  - Total produits: " + totalProducts);
        System.out.println("  - Stock faible: " + lowStockCount);
        System.out.println("  - Rupture de stock: " + outOfStockCount);
        System.out.println("  - Mouvements aujourd'hui: " + todayMovements);
        System.out.println("  - Alertes en attente: " + pendingAlerts);
        
        return new MagasinierDashboardSummary(
                totalProducts,
                lowStockCount,
                outOfStockCount,
                todayMovements,
                pendingAlerts
        );
    }

    /**
     * Compte le nombre total de produits actifs
     */
    private int getTotalProducts() {
        return (int) produitRepository.count();
    }

    /**
     * Compte les produits avec stock faible (quantité <= seuil d'alerte)
     */
    private int getLowStockCount() {
        return (int) stockRepository.countProduitsFaible();
    }

    /**
     * Compte les produits en rupture de stock (quantité = 0)
     */
    private int getOutOfStockCount() {
        return (int) stockRepository.countProduitsEnRupture();
    }

    /**
     * Compte les mouvements de stock d'aujourd'hui (entrées + sorties)
     */
    private int getTodayStockMovements() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        // Compter les entrées d'aujourd'hui
        int entrees = entreeStockRepository.findEntreesParPeriode(startOfDay, endOfDay).size();
        
        // Compter les sorties d'aujourd'hui
        int sorties = sortieStockRepository.findSortiesParPeriode(startOfDay, endOfDay).size();
        
        return entrees + sorties;
    }

    /**
     * Compte les alertes non lues (statut NON_LU)
     */
    private int getPendingAlerts() {
        Long count = alerteStockRepository.countByStatut(AlerteStock.StatutAlerte.NON_LU);
        return count != null ? count.intValue() : 0;
    }
}