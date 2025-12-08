package com.gestiondestock.controller;

import com.gestiondestock.dto.*;
import com.gestiondestock.service.StatistiquesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
@CrossOrigin(origins = "*") // À ajuster selon vos besoins
public class StatistiquesController {

    private static final Logger logger = LoggerFactory.getLogger(StatistiquesController.class);

    @Autowired
    private StatistiquesService statistiquesService;

    /**
     * Récupère les statistiques générales de l'application
     * GET /api/statistiques/generales
     */
    @GetMapping("/generales")
    public ResponseEntity<StatistiquesGeneralesDTO> getStatistiquesGenerales() {
        try {
            logger.info("Récupération des statistiques générales");
            StatistiquesGeneralesDTO stats = statistiquesService.getStatistiquesGenerales();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des statistiques générales", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les statistiques des ventes
     * GET /api/statistiques/ventes
     */
    @GetMapping("/ventes")
    public ResponseEntity<StatistiquesVentesDTO> getStatistiquesVentes() {
        try {
            logger.info("Récupération des statistiques de ventes");
            StatistiquesVentesDTO stats = statistiquesService.getStatistiquesVentes();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des statistiques de ventes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère le top des produits les plus vendus
     * GET /api/statistiques/top-produits?limit=5
     */
    @GetMapping("/top-produits")
    public ResponseEntity<List<ProduitVenduDTO>> getTopProduits(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            logger.info("Récupération du top {} produits", limit);
            if (limit <= 0 || limit > 100) {
                return ResponseEntity.badRequest().build();
            }
            List<ProduitVenduDTO> topProduits = statistiquesService.getTopProduitsVendus(limit);
            return ResponseEntity.ok(topProduits);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du top produits", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère le top des meilleurs clients
     * GET /api/statistiques/top-clients?limit=5
     */
    @GetMapping("/top-clients")
    public ResponseEntity<List<ClientTopDTO>> getTopClients(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            logger.info("Récupération du top {} clients", limit);
            if (limit <= 0 || limit > 100) {
                return ResponseEntity.badRequest().build();
            }
            List<ClientTopDTO> topClients = statistiquesService.getTopClients(limit);
            return ResponseEntity.ok(topClients);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du top clients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les statistiques de stock
     * GET /api/statistiques/stock
     */
    @GetMapping("/stock")
    public ResponseEntity<StatistiquesStockDTO> getStatistiquesStock() {
        try {
            logger.info("Récupération des statistiques de stock");
            StatistiquesStockDTO stats = statistiquesService.getStatistiquesStock();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des statistiques de stock", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les statistiques des commandes
     * GET /api/statistiques/commandes
     */
    @GetMapping("/commandes")
    public ResponseEntity<StatistiquesCommandesDTO> getStatistiquesCommandes() {
        try {
            logger.info("Récupération des statistiques de commandes");
            StatistiquesCommandesDTO stats = statistiquesService.getStatistiquesCommandes();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des statistiques de commandes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère l'évolution des ventes dans le temps
     * GET /api/statistiques/evolution-ventes
     */
    @GetMapping("/evolution-ventes")
    public ResponseEntity<EvolutionVentesDTO> getEvolutionVentes() {
        try {
            logger.info("Récupération de l'évolution des ventes");
            EvolutionVentesDTO evolution = statistiquesService.getEvolutionVentes();
            return ResponseEntity.ok(evolution);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'évolution des ventes", e);
            // Renvoie un objet vide au lieu d'un 500 pour éviter de casser le frontend
            EvolutionVentesDTO empty = new EvolutionVentesDTO();
            empty.setVentesParJour(java.util.Collections.emptyList());
            empty.setVentesParMois(java.util.Collections.emptyList());
            empty.setVentesParAnnee(java.util.Collections.emptyList());
            return ResponseEntity.ok(empty);
        }
    }

    /**
     * Récupère toutes les données du dashboard
     * GET /api/statistiques/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardComplet() {
        try {
            logger.info("Récupération du dashboard complet");
            DashboardDTO dashboard = statistiquesService.getDashboardComplet();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check pour vérifier que l'API fonctionne
     * GET /api/statistiques/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API Statistiques opérationnelle");
    }
}