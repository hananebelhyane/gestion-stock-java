package com.gestiondestock.service;

import com.gestiondestock.dto.*;
import com.gestiondestock.entity.*;
import com.gestiondestock.exception.StockInsuffisantException;
import com.gestiondestock.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;
    private final EntreeStockRepository entreeStockRepository;
    private final SortieStockRepository sortieStockRepository;
    private final MagasinierRepository magasinierRepository;
    private final AlerteStockRepository alerteStockRepository;

    public StockService(StockRepository stockRepository, 
                       ProduitRepository produitRepository,
                       EntreeStockRepository entreeStockRepository,
                       SortieStockRepository sortieStockRepository,
                       MagasinierRepository magasinierRepository,
                       AlerteStockRepository alerteStockRepository) {
        this.stockRepository = stockRepository;
        this.produitRepository = produitRepository;
        this.entreeStockRepository = entreeStockRepository;
        this.sortieStockRepository = sortieStockRepository;
        this.magasinierRepository = magasinierRepository;
        this.alerteStockRepository = alerteStockRepository;
    }

    // ========== MÉTHODES EXISTANTES (Admin - Consultation) ==========
    
    public List<Stock> getStock() {
        return stockRepository.findAllWithProduit();
    }

    public Stock getStockByIdProduit(UUID produitId) {
        return stockRepository.findByProduitId(produitId);
    }

    public Stock createStock(UUID produitId, Integer quantiteDisponible, Integer seuilAlerte) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("PRODUIT_INEXISTANT"));
        Stock stock = new Stock();
        stock.setProduit(produit);
        stock.setQuantiteDisponible(quantiteDisponible);
        stock.setSeuilAlerte(seuilAlerte);
        Stock savedStock = stockRepository.save(stock);
        
        // Vérifier et créer alerte si nécessaire
        verifierEtCreerAlerte(savedStock);
        
        return savedStock;
    }

    public Stock saveStock(Stock stock) {
        UUID produitId = stock.getProduit().getId();
        Produit p = produitRepository.findById(produitId).orElse(null);
        if (p == null) {
            throw new RuntimeException("PRODUIT_INEXISTANT");
        }
        stock.setProduit(p);
        Stock savedStock = stockRepository.save(stock);
        
        // Vérifier et créer alerte si nécessaire
        verifierEtCreerAlerte(savedStock);
        
        return savedStock;
    }

    public Stock updateStock(UUID stockId, Stock updatedStock) {
        Stock existingStock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("STOCK_NOT_FOUND"));
        existingStock.setQuantiteDisponible(updatedStock.getQuantiteDisponible());
        existingStock.setSeuilAlerte(updatedStock.getSeuilAlerte());
        existingStock.setProduit(updatedStock.getProduit());
        Stock saved = stockRepository.save(existingStock);
        
        // Vérifier et créer alerte si nécessaire
        verifierEtCreerAlerte(saved);
        
        return saved;
    }

    public void deleteStock(UUID stockId) {
        stockRepository.deleteById(stockId);
    }

    // ========== NOUVELLES MÉTHODES POUR LE MAGASINIER ==========
    
    /**
     * Enregistrer une entrée de stock
     */
    @Transactional
    public EntreeStockDTO enregistrerEntree(EntreeStockDTO entreeDTO, UUID magasinierId) {
        
        // Vérifier que le produit existe
        Produit produit = produitRepository.findById(entreeDTO.getProduitId())
                .orElseThrow(() -> new RuntimeException("PRODUIT_INEXISTANT"));
        
        // Vérifier que le magasinier existe
        Magasinier magasinier = magasinierRepository.findById(magasinierId)
                .orElseThrow(() -> new RuntimeException("MAGASINIER_INEXISTANT"));
        
        // Créer l'entrée de stock
        EntreeStock entree = new EntreeStock();
        entree.setProduit(produit);
        entree.setQuantite(entreeDTO.getQuantite());
        entree.setMagasinier(magasinier);
        entree.setDate_entree(LocalDateTime.now());
        
        // Si une commande fournisseur est spécifiée, l'associer
        if (entreeDTO.getCommandeFournisseurId() != null) {
            // Vous devrez ajouter CommandeFournisseurRepository si ce n'est pas déjà fait
            // CommandeFournisseur cf = commandeFournisseurRepository.findById(entreeDTO.getCommandeFournisseurId())
            //     .orElseThrow(() -> new RuntimeException("COMMANDE_FOURNISSEUR_INEXISTANTE"));
            // entree.setCommandefournisseur(cf);
        }
        
        // Sauvegarder l'entrée
        EntreeStock savedEntree = entreeStockRepository.save(entree);
        
        // Mettre à jour le stock
        Stock stock = stockRepository.findByProduitId(entreeDTO.getProduitId());
        if (stock == null) {
            // Si le stock n'existe pas encore, le créer
            stock = new Stock();
            stock.setProduit(produit);
            stock.setQuantiteDisponible(entreeDTO.getQuantite());
            stock.setSeuilAlerte(10); // Valeur par défaut
        } else {
            // Ajouter la quantité au stock existant
            stock.setQuantiteDisponible(stock.getQuantiteDisponible() + entreeDTO.getQuantite());
        }
        Stock savedStock = stockRepository.save(stock);
        
        // Vérifier et créer alerte si nécessaire (peut résoudre une alerte existante)
        verifierEtCreerAlerte(savedStock);
        
        // Convertir en DTO et retourner
        return convertEntreeToDTO(savedEntree);
    }
    
    /**
     * Enregistrer une sortie de stock
     */
    @Transactional
    public SortieStockDTO enregistrerSortie(SortieStockDTO sortieDTO, UUID magasinierId) {
        
        // Vérifier que le produit existe
        Produit produit = produitRepository.findById(sortieDTO.getProduitId())
                .orElseThrow(() -> new RuntimeException("PRODUIT_INEXISTANT"));
        
        // Vérifier que le magasinier existe
        Magasinier magasinier = magasinierRepository.findById(magasinierId)
                .orElseThrow(() -> new RuntimeException("MAGASINIER_INEXISTANT"));
        
        // Vérifier le stock disponible
        Stock stock = stockRepository.findByProduitId(sortieDTO.getProduitId());
        if (stock == null) {
            throw new StockInsuffisantException("Aucun stock trouvé pour ce produit");
        }
        
        if (stock.getQuantiteDisponible() < sortieDTO.getQuantite()) {
            throw new StockInsuffisantException(
                String.format("Stock insuffisant pour le produit '%s'. Disponible: %d, Demandé: %d",
                    produit.getNom(),
                    stock.getQuantiteDisponible(),
                    sortieDTO.getQuantite())
            );
        }
        
        // Créer la sortie de stock
        SortieStock sortie = new SortieStock();
        sortie.setProduit(produit);
        sortie.setQuantite(sortieDTO.getQuantite());
        sortie.setMagasinier(magasinier);
        sortie.setDate_sortie(LocalDateTime.now());
        
        // Si une ligne de commande est spécifiée, l'associer
        if (sortieDTO.getLigneCommandeId() != null) {
            // Vous devrez ajouter LigneCommandeRepository si ce n'est pas déjà fait
            // LigneCommande lc = ligneCommandeRepository.findById(sortieDTO.getLigneCommandeId())
            //     .orElseThrow(() -> new RuntimeException("LIGNE_COMMANDE_INEXISTANTE"));
            // sortie.setLignecommande(lc);
        }
        
        // Sauvegarder la sortie
        SortieStock savedSortie = sortieStockRepository.save(sortie);
        
        // Mettre à jour le stock (soustraire la quantité)
        stock.setQuantiteDisponible(stock.getQuantiteDisponible() - sortieDTO.getQuantite());
        Stock savedStock = stockRepository.save(stock);
        
        // ✅ IMPORTANT : Vérifier et créer alerte automatiquement si stock faible
        verifierEtCreerAlerte(savedStock);
        
        // Convertir en DTO et retourner
        return convertSortieToDTO(savedSortie);
    }
    
    /**
     * Obtenir les produits en alerte
     */
    public List<StockAlertDTO> getProduitsEnAlerte() {
        List<Stock> stocksFaibles = stockRepository.findProduitsStockFaible();
        List<Stock> stocksRupture = stockRepository.findProduitsEnRupture();
        
        List<StockAlertDTO> alertes = new ArrayList<>();
        
        // Ajouter les stocks en rupture
        for (Stock stock : stocksRupture) {
            alertes.add(convertStockToAlertDTO(stock));
        }
        
        // Ajouter les stocks faibles
        for (Stock stock : stocksFaibles) {
            alertes.add(convertStockToAlertDTO(stock));
        }
        
        return alertes;
    }
    
    /**
     * Obtenir l'historique des entrées d'un produit
     */
    public List<EntreeStockDTO> getHistoriqueEntreesProduit(UUID produitId) {
        return entreeStockRepository.findByProduitId(produitId)
                .stream()
                .map(this::convertEntreeToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir l'historique des sorties d'un produit
     */
    public List<SortieStockDTO> getHistoriqueSortiesProduit(UUID produitId) {
        return sortieStockRepository.findByProduitId(produitId)
                .stream()
                .map(this::convertSortieToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir tous les mouvements du jour
     */
    public List<MouvementStockDTO> getMouvementsDuJour() {
        List<MouvementStockDTO> mouvements = new ArrayList<>();
        
        // Ajouter les entrées du jour
        List<EntreeStock> entrees = entreeStockRepository.findEntreesDuJour();
        for (EntreeStock entree : entrees) {
            mouvements.add(convertEntreeToMouvementDTO(entree));
        }
        
        // Ajouter les sorties du jour
        List<SortieStock> sorties = sortieStockRepository.findSortiesDuJour();
        for (SortieStock sortie : sorties) {
            mouvements.add(convertSortieToMouvementDTO(sortie));
        }
        
        // Trier par date décroissante
        mouvements.sort((m1, m2) -> m2.getDateMouvement().compareTo(m1.getDateMouvement()));
        
        return mouvements;
    }
    
    // ========== ✅ MÉTHODE PRINCIPALE : VÉRIFIER ET CRÉER ALERTE ==========
    
    /**
     * Vérifier si le stock est faible et créer une alerte automatiquement
     * Cette méthode est appelée après chaque modification de stock
     */
    private void verifierEtCreerAlerte(Stock stock) {
        Integer quantite = stock.getQuantiteDisponible();
        Integer seuil = stock.getSeuilAlerte();
        
        // 1. Déterminer le niveau de gravité
        AlerteStock.NiveauGravite gravite = null;
        String message = null;
        
        if (quantite == 0) {
            // RUPTURE DE STOCK
            gravite = AlerteStock.NiveauGravite.CRITIQUE;
            message = String.format(
                "🔴 URGENT : Rupture de stock pour '%s' (0 unité disponible)",
                stock.getProduit().getNom()
            );
        } else if (quantite <= seuil / 2) {
            // STOCK CRITIQUE (moins de la moitié du seuil)
            gravite = AlerteStock.NiveauGravite.MOYENNE;
            message = String.format(
                "⚠️ Stock critique pour '%s' (%d/%d unités)",
                stock.getProduit().getNom(),
                quantite,
                seuil
            );
        } else if (quantite <= seuil) {
            // STOCK FAIBLE
            gravite = AlerteStock.NiveauGravite.FAIBLE;
            message = String.format(
                "⚡ Stock faible pour '%s' (%d/%d unités)",
                stock.getProduit().getNom(),
                quantite,
                seuil
            );
        }
        
        // 2. Si pas d'alerte nécessaire, on sort
        if (gravite == null) {
            System.out.println("ℹ️ Stock OK pour " + stock.getProduit().getNom() + " - Pas d'alerte nécessaire");
            return;
        }
        
        // 3. Vérifier qu'une alerte de ce niveau n'existe pas déjà
        Boolean alerteExiste = alerteStockRepository.existsAlerteNonLuePourProduit(
            stock.getProduit().getId(),
            gravite
        );
        
        if (!alerteExiste) {
            // 4. Créer la nouvelle alerte
            AlerteStock alerte = new AlerteStock(
                stock.getProduit(),
                message,
                gravite,
                quantite,
                seuil
            );
            
            alerteStockRepository.save(alerte);
            
            System.out.println("✅ Alerte créée automatiquement : " + message);
        } else {
            System.out.println("ℹ️ Alerte déjà existante pour " + stock.getProduit().getNom());
        }
    }
    
    // ========== MÉTHODES DE CONVERSION ==========
    
    private EntreeStockDTO convertEntreeToDTO(EntreeStock entree) {
        EntreeStockDTO dto = new EntreeStockDTO();
        dto.setId(entree.getId());
        dto.setProduitId(entree.getProduit().getId());
        dto.setProduitNom(entree.getProduit().getNom());
        dto.setQuantite(entree.getQuantite());
        dto.setDateEntree(entree.getDate_entree());
        
        if (entree.getMagasinier() != null) {
            dto.setMagasinierId(entree.getMagasinier().getId());
            dto.setMagasinierNom(entree.getMagasinier().getNom() + " " + entree.getMagasinier().getPrenom());
        }
        
        if (entree.getCommandefournisseur() != null) {
            dto.setCommandeFournisseurId(entree.getCommandefournisseur().getId());
            // Ajoutez la référence si disponible dans CommandeFournisseur
        }
        
        return dto;
    }
    
    private SortieStockDTO convertSortieToDTO(SortieStock sortie) {
        SortieStockDTO dto = new SortieStockDTO();
        dto.setId(sortie.getId());
        dto.setProduitId(sortie.getProduit().getId());
        dto.setProduitNom(sortie.getProduit().getNom());
        dto.setQuantite(sortie.getQuantite());
        dto.setDateSortie(sortie.getDate_sortie());
        
        if (sortie.getMagasinier() != null) {
            dto.setMagasinierId(sortie.getMagasinier().getId());
            dto.setMagasinierNom(sortie.getMagasinier().getNom() + " " + sortie.getMagasinier().getPrenom());
        }
        
        if (sortie.getLignecommande() != null) {
            dto.setLigneCommandeId(sortie.getLignecommande().getId());
            // Ajoutez la référence si disponible dans LigneCommande
        }
        
        return dto;
    }
    
    private MouvementStockDTO convertEntreeToMouvementDTO(EntreeStock entree) {
        MouvementStockDTO dto = new MouvementStockDTO();
        dto.setId(entree.getId());
        dto.setType("ENTREE");
        dto.setProduitId(entree.getProduit().getId());
        dto.setProduitNom(entree.getProduit().getNom());
        dto.setQuantite(entree.getQuantite());
        dto.setDateMouvement(entree.getDate_entree());
        
        if (entree.getMagasinier() != null) {
            dto.setMagasinierNom(entree.getMagasinier().getNom() + " " + entree.getMagasinier().getPrenom());
        }
        
        if (entree.getCommandefournisseur() != null) {
            dto.setReference("CF-" + entree.getCommandefournisseur().getId());
        }
        
        return dto;
    }
    
    private MouvementStockDTO convertSortieToMouvementDTO(SortieStock sortie) {
        MouvementStockDTO dto = new MouvementStockDTO();
        dto.setId(sortie.getId());
        dto.setType("SORTIE");
        dto.setProduitId(sortie.getProduit().getId());
        dto.setProduitNom(sortie.getProduit().getNom());
        dto.setQuantite(sortie.getQuantite());
        dto.setDateMouvement(sortie.getDate_sortie());
        
        if (sortie.getMagasinier() != null) {
            dto.setMagasinierNom(sortie.getMagasinier().getNom() + " " + sortie.getMagasinier().getPrenom());
        }
        
        if (sortie.getLignecommande() != null) {
            dto.setReference("LC-" + sortie.getLignecommande().getId());
        }
        
        return dto;
    }
    
    private StockAlertDTO convertStockToAlertDTO(Stock stock) {
        return new StockAlertDTO(
            stock.getId(),
            stock.getProduit().getId(),
            stock.getProduit().getNom(),
            stock.getQuantiteDisponible(),
            stock.getSeuilAlerte()
        );
    }
}