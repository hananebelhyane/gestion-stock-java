package com.gestiondestock.controller;

import com.gestiondestock.entity.AlerteStock;
import com.gestiondestock.service.AlerteStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des alertes de stock
 * Accessible par ADMIN et MAGASINIER
 */
@RestController
@RequestMapping("/api/alertes")
public class AlerteStockController {

    private final AlerteStockService alerteService;

    public AlerteStockController(AlerteStockService alerteService) {
        this.alerteService = alerteService;
    }

    // ========== CONSULTATION DES ALERTES ==========

    /**
     * GET /api/alertes
     * Obtenir toutes les alertes triées par date
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<AlerteStock>> getToutesLesAlertes() {
        return ResponseEntity.ok(alerteService.getToutesLesAlertes());
    }

    /**
     * GET /api/alertes/non-lues
     * Obtenir les alertes non lues (triées par gravité)
     */
    @GetMapping("/non-lues")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<AlerteStock>> getAlertesNonLues() {
        return ResponseEntity.ok(alerteService.getAlertesNonLues());
    }

    /**
     * GET /api/alertes/du-jour
     * Obtenir les alertes créées aujourd'hui
     */
    @GetMapping("/du-jour")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<AlerteStock>> getAlertesDuJour() {
        return ResponseEntity.ok(alerteService.getAlertesDuJour());
    }

    /**
     * GET /api/alertes/gravite/{niveau}
     * Filtrer par niveau de gravité (CRITIQUE, MOYENNE, FAIBLE)
     */
    @GetMapping("/gravite/{niveau}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<AlerteStock>> getAlertesParGravite(
            @PathVariable String niveau) {
        try {
            AlerteStock.NiveauGravite gravite = AlerteStock.NiveauGravite.valueOf(niveau.toUpperCase());
            return ResponseEntity.ok(alerteService.getAlertesParGravite(gravite));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/alertes/count
     * Compter les alertes non lues
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<Long> compterAlertesNonLues() {
        return ResponseEntity.ok(alerteService.compterAlertesNonLues());
    }

    /**
     * GET /api/alertes/statistiques
     * Statistiques détaillées des alertes
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<AlerteStockService.StatistiquesAlertesDTO> getStatistiques() {
        return ResponseEntity.ok(alerteService.getStatistiques());
    }

    // ========== TRAITEMENT DES ALERTES ==========

    /**
     * PUT /api/alertes/{alerteId}/traiter
     * Marquer une alerte comme traitée
     */
    @PutMapping("/{alerteId}/traiter")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<AlerteStock> traiterAlerte(@PathVariable UUID alerteId) {
        try {
            AlerteStock alerte = alerteService.traiterAlerte(alerteId);
            return ResponseEntity.ok(alerte);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/alertes/traiter-toutes
     * Marquer toutes les alertes comme traitées
     */
    @PutMapping("/traiter-toutes")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<String> traiterToutesLesAlertes() {
        int count = alerteService.traiterToutesLesAlertes();
        return ResponseEntity.ok(count + " alerte(s) traitée(s)");
    }

    /**
     * DELETE /api/alertes/{alerteId}
     * Supprimer une alerte
     */
    @DeleteMapping("/{alerteId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<Void> supprimerAlerte(@PathVariable UUID alerteId) {
        try {
            alerteService.supprimerAlerte(alerteId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GÉNÉRATION AUTOMATIQUE ==========

    /**
     * POST /api/alertes/generer
     * Vérifier tous les stocks et créer les alertes nécessaires
     * (Bouton "Générer Alertes" dans l'interface)
     */
    @PostMapping("/generer")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<AlerteStock>> genererAlertes() {
        List<AlerteStock> nouvellesAlertes = alerteService.verifierEtCreerAlertes();
        return ResponseEntity.ok(nouvellesAlertes);
    }
}