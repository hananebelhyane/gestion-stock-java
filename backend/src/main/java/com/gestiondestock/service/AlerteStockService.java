package com.gestiondestock.service;

import com.gestiondestock.entity.AlerteStock;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.entity.Stock;
import com.gestiondestock.repository.AlerteStockRepository;
import com.gestiondestock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des alertes de stock
 * Logique métier : création automatique, traitement, statistiques
 */
@Service
public class AlerteStockService {

    private final AlerteStockRepository alerteRepository;
    private final StockRepository stockRepository;

    public AlerteStockService(AlerteStockRepository alerteRepository, 
                             StockRepository stockRepository) {
        this.alerteRepository = alerteRepository;
        this.stockRepository = stockRepository;
    }

    // ========== CONSULTATION DES ALERTES ==========

    /**
     * Toutes les alertes triées par date
     */
    public List<AlerteStock> getToutesLesAlertes() {
        return alerteRepository.findAllByOrderByDateAlerteDesc();
    }

    /**
     * Alertes non lues uniquement (triées par gravité)
     */
    public List<AlerteStock> getAlertesNonLues() {
        return alerteRepository.findByStatutOrderByGravite(AlerteStock.StatutAlerte.NON_LU);
    }

    /**
     * Alertes du jour
     */
    public List<AlerteStock> getAlertesDuJour() {
        return alerteRepository.findAlertesDuJour();
    }

    /**
     * Alertes par niveau de gravité
     */
    public List<AlerteStock> getAlertesParGravite(AlerteStock.NiveauGravite gravite) {
        return alerteRepository.findByNiveauGravite(gravite);
    }

    /**
     * Compter les alertes non lues
     */
    public Long compterAlertesNonLues() {
        return alerteRepository.countByStatut(AlerteStock.StatutAlerte.NON_LU);
    }

    // ========== TRAITEMENT DES ALERTES ==========

    /**
     * Marquer une alerte comme traitée
     */
    @Transactional
    public AlerteStock traiterAlerte(UUID alerteId) {
        AlerteStock alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable avec l'ID: " + alerteId));
        
        alerte.setStatut(AlerteStock.StatutAlerte.TRAITE);
        return alerteRepository.save(alerte);
    }

    /**
     * Supprimer une alerte
     */
    @Transactional
    public void supprimerAlerte(UUID alerteId) {
        if (!alerteRepository.existsById(alerteId)) {
            throw new RuntimeException("Alerte introuvable avec l'ID: " + alerteId);
        }
        alerteRepository.deleteById(alerteId);
    }

    /**
     * Marquer toutes les alertes comme traitées
     */
    @Transactional
    public int traiterToutesLesAlertes() {
        List<AlerteStock> alertes = alerteRepository.findByStatutOrderByGravite(
            AlerteStock.StatutAlerte.NON_LU
        );
        alertes.forEach(a -> a.setStatut(AlerteStock.StatutAlerte.TRAITE));
        alerteRepository.saveAll(alertes);
        return alertes.size();
    }

    // ========== GÉNÉRATION AUTOMATIQUE DES ALERTES ==========

    /**
     * Vérifier et créer des alertes pour tous les stocks faibles/ruptures
     * Appelé manuellement par le magasinier ou automatiquement (scheduler)
     */
    @Transactional
    public List<AlerteStock> verifierEtCreerAlertes() {
        List<AlerteStock> nouvellesAlertes = new ArrayList<>();
        
        // 1. Produits en RUPTURE DE STOCK (quantité = 0)
        List<Stock> stocksRupture = stockRepository.findProduitsEnRupture();
        for (Stock stock : stocksRupture) {
            AlerteStock alerte = creerAlerteRupture(stock);
            if (alerte != null) {
                nouvellesAlertes.add(alerte);
            }
        }
        
        // 2. Produits en STOCK FAIBLE (quantité <= seuil)
        List<Stock> stocksFaibles = stockRepository.findProduitsStockFaible();
        for (Stock stock : stocksFaibles) {
            AlerteStock alerte = creerAlerteStockFaible(stock);
            if (alerte != null) {
                nouvellesAlertes.add(alerte);
            }
        }
        
        return nouvellesAlertes;
    }

    /**
     * Vérifier et créer une alerte après une modification de stock
     * Appelé automatiquement après une sortie de stock
     */
    @Transactional
    public void verifierEtCreerAlerteApresModification(Stock stock) {
        Integer quantite = stock.getQuantiteDisponible();
        Integer seuil = stock.getSeuilAlerte();
        
        if (quantite == 0) {
            creerAlerteRupture(stock);
        } else if (quantite <= seuil) {
            creerAlerteStockFaible(stock);
        }
    }

    // ========== CRÉATION D'ALERTES ==========

    /**
     * Créer une alerte pour rupture de stock
     */
    @Transactional
    public AlerteStock creerAlerteRupture(Stock stock) {
        // Vérifier si une alerte CRITIQUE non lue existe déjà
        if (alerteRepository.existsAlerteNonLuePourProduit(
                stock.getProduit().getId(), 
                AlerteStock.NiveauGravite.CRITIQUE)) {
            return null; // Alerte déjà existante
        }
        
        String message = String.format(
            "🔴 URGENT : Rupture de stock pour '%s' (0 unité disponible)",
            stock.getProduit().getNom()
        );
        
        AlerteStock alerte = new AlerteStock(
            stock.getProduit(),
            message,
            AlerteStock.NiveauGravite.CRITIQUE,
            0,
            stock.getSeuilAlerte()
        );
        
        return alerteRepository.save(alerte);
    }

    /**
     * Créer une alerte pour stock faible
     */
    @Transactional
    public AlerteStock creerAlerteStockFaible(Stock stock) {
        Integer quantite = stock.getQuantiteDisponible();
        Integer seuil = stock.getSeuilAlerte();
        
        // Déterminer le niveau de gravité
        AlerteStock.NiveauGravite gravite;
        String emoji;
        
        if (quantite <= seuil / 2) {
            gravite = AlerteStock.NiveauGravite.MOYENNE;
            emoji = "⚠️";
        } else {
            gravite = AlerteStock.NiveauGravite.FAIBLE;
            emoji = "⚡";
        }
        
        // Vérifier si une alerte de ce niveau existe déjà
        if (alerteRepository.existsAlerteNonLuePourProduit(
                stock.getProduit().getId(), gravite)) {
            return null;
        }
        
        String message = String.format(
            "%s Stock %s pour '%s' (%d/%d unités)",
            emoji,
            gravite == AlerteStock.NiveauGravite.MOYENNE ? "critique" : "faible",
            stock.getProduit().getNom(),
            quantite,
            seuil
        );
        
        AlerteStock alerte = new AlerteStock(
            stock.getProduit(),
            message,
            gravite,
            quantite,
            seuil
        );
        
        return alerteRepository.save(alerte);
    }

    /**
     * Créer une alerte manuelle (pour des cas spéciaux)
     */
    @Transactional
    public AlerteStock creerAlerteManuelle(Produit produit, String message, 
                                          AlerteStock.NiveauGravite gravite) {
        AlerteStock alerte = new AlerteStock();
        alerte.setProduit(produit);
        alerte.setMessage(message);
        alerte.setNiveauGravite(gravite);
        alerte.setDateAlerte(LocalDateTime.now());
        alerte.setStatut(AlerteStock.StatutAlerte.NON_LU);
        
        return alerteRepository.save(alerte);
    }

    // ========== STATISTIQUES ==========

    /**
     * Statistiques des alertes non lues
     */
    public StatistiquesAlertesDTO getStatistiques() {
        long total = compterAlertesNonLues();
        long critiques = alerteRepository.countByNiveauGraviteAndStatut(
            AlerteStock.NiveauGravite.CRITIQUE, 
            AlerteStock.StatutAlerte.NON_LU
        );
        long moyennes = alerteRepository.countByNiveauGraviteAndStatut(
            AlerteStock.NiveauGravite.MOYENNE, 
            AlerteStock.StatutAlerte.NON_LU
        );
        long faibles = alerteRepository.countByNiveauGraviteAndStatut(
            AlerteStock.NiveauGravite.FAIBLE, 
            AlerteStock.StatutAlerte.NON_LU
        );
        
        return new StatistiquesAlertesDTO(total, critiques, moyennes, faibles);
    }

    // ========== DTO INTERNE ==========
    
    public static class StatistiquesAlertesDTO {
        public long total;
        public long critiques;
        public long moyennes;
        public long faibles;
        
        public StatistiquesAlertesDTO(long total, long critiques, long moyennes, long faibles) {
            this.total = total;
            this.critiques = critiques;
            this.moyennes = moyennes;
            this.faibles = faibles;
        }
    }
}