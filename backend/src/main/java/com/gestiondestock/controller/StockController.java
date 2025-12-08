package com.gestiondestock.controller;

import com.gestiondestock.dto.*;
import com.gestiondestock.entity.Magasinier;
import com.gestiondestock.entity.Stock;
import com.gestiondestock.exception.StockInsuffisantException;
import com.gestiondestock.repository.MagasinierRepository;
import com.gestiondestock.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;
    private final MagasinierRepository magasinierRepository;

    public StockController(StockService stockService, MagasinierRepository magasinierRepository) {
        this.stockService = stockService;
        this.magasinierRepository = magasinierRepository;
    }

    // ========== MÉTHODES ADMIN (Consultation) - Code de votre collègue ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public List<Stock> getAllStock() {
        return stockService.getStock();
    }

    @GetMapping("/{produitId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock getStockByProduit(@PathVariable UUID produitId) {
        return stockService.getStockByIdProduit(produitId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock createStock(
            @RequestParam UUID produitId,
            @RequestParam Integer quantiteDisponible,
            @RequestParam Integer seuilAlerte) {
        return stockService.createStock(produitId, quantiteDisponible, seuilAlerte);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock addStock(@RequestBody Stock stock) {
        return stockService.saveStock(stock);
    }

    @DeleteMapping("/{stockId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public void deleteStock(@PathVariable UUID stockId) {
        stockService.deleteStock(stockId);
    }

    @PutMapping("/{stockId}")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public Stock updateStock(@PathVariable UUID stockId, @RequestBody Stock stock) {
        return stockService.updateStock(stockId, stock);
    }

    // ========== NOUVELLES MÉTHODES MAGASINIER (Votre tâche) ==========

    /**
     * Enregistrer une entrée de stock
     */
    @PostMapping("/entrees")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<?> enregistrerEntree(@RequestBody EntreeStockDTO entreeDTO) {
        try {
            UUID magasinierId = getMagasinierIdFromAuth();
            EntreeStockDTO result = stockService.enregistrerEntree(entreeDTO, magasinierId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Enregistrer une sortie de stock
     */
    @PostMapping("/sorties")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<?> enregistrerSortie(@RequestBody SortieStockDTO sortieDTO) {
        try {
            UUID magasinierId = getMagasinierIdFromAuth();
            SortieStockDTO result = stockService.enregistrerSortie(sortieDTO, magasinierId);
            return ResponseEntity.ok(result);
        } catch (StockInsuffisantException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtenir les produits en alerte
     */
    @GetMapping("/alertes")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<StockAlertDTO>> getProduitsEnAlerte() {
        return ResponseEntity.ok(stockService.getProduitsEnAlerte());
    }

    /**
     * Obtenir l'historique des entrées d'un produit
     */
    @GetMapping("/produit/{produitId}/entrees")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<EntreeStockDTO>> getHistoriqueEntrees(@PathVariable UUID produitId) {
        return ResponseEntity.ok(stockService.getHistoriqueEntreesProduit(produitId));
    }

    /**
     * Obtenir l'historique des sorties d'un produit
     */
    @GetMapping("/produit/{produitId}/sorties")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<SortieStockDTO>> getHistoriqueSorties(@PathVariable UUID produitId) {
        return ResponseEntity.ok(stockService.getHistoriqueSortiesProduit(produitId));
    }

    /**
     * Obtenir tous les mouvements du jour
     */
    @GetMapping("/mouvements/aujourd-hui")
    @PreAuthorize("hasAnyRole('ADMIN','MAGASINIER')")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsDuJour() {
        return ResponseEntity.ok(stockService.getMouvementsDuJour());
    }

    // ========== MÉTHODE UTILITAIRE ==========

    /**
     * Récupère l'ID du magasinier connecté depuis le contexte de sécurité
     */
    private UUID getMagasinierIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String username = (String) authentication.getPrincipal();
        
        Magasinier magasinier = magasinierRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Magasinier non trouvé: " + username));
        
        return magasinier.getId();
    }
}