package com.gestiondestock.service;

import com.gestiondestock.entity.Categorie;
import com.gestiondestock.entity.Fournisseur;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.exception.ResourceNotFoundException;
import com.gestiondestock.repository.CategorieRepository;
import com.gestiondestock.repository.FournisseurRepository;
import com.gestiondestock.repository.ProduitRepository;
import com.gestiondestock.repository.StockRepository;
import com.gestiondestock.repository.CommandeFournisseurRepository;
import com.gestiondestock.repository.LigneCommandeRepository;
import com.gestiondestock.repository.AlerteStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final FournisseurRepository fournisseurRepository;
    private final StockRepository stockRepository;
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final AlerteStockRepository alerteStockRepository;
    private final com.gestiondestock.repository.EntreeStockRepository entreeStockRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository,
                              CategorieRepository categorieRepository,
                              FournisseurRepository fournisseurRepository,
                              StockRepository stockRepository,
                              CommandeFournisseurRepository commandeFournisseurRepository,
                              LigneCommandeRepository ligneCommandeRepository,
                              AlerteStockRepository alerteStockRepository,
                              com.gestiondestock.repository.EntreeStockRepository entreeStockRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.stockRepository = stockRepository;
        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.alerteStockRepository = alerteStockRepository;
        this.entreeStockRepository = entreeStockRepository;
    }

    @Override
    @Transactional
    public Produit createProduit(Produit produit) {
        validateProduit(produit);
        attachRelations(produit);
        return produitRepository.save(produit);
    }

    @Override
    @Transactional
    public Produit updateProduit(UUID id, Produit produit) {
        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        validateProduit(produit);

        existing.setNom(produit.getNom());
        existing.setDescription(produit.getDescription());
        existing.setPrixUnitaire(produit.getPrixUnitaire());
        existing.setUrlImage(produit.getUrlImage());

        // Réattacher les relations de manière sécurisée
        Categorie cat = resolveCategorie(produit.getCategorie());
        Fournisseur four = resolveFournisseur(produit.getFournisseur());
        existing.setCategorie(cat);
        existing.setFournisseur(four);

        return produitRepository.save(existing);
    }

    @Override
    public Produit getProduitById(UUID id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
    }

    @Override
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    @Override
    public List<Produit> getProduitsByCategorie(UUID categorieId) {
        if (categorieId == null) {
            return getAllProduits();
        }
        return produitRepository.findByCategorieId(categorieId);
    }

    @Override
    @Transactional
    public void deleteProduit(UUID id) {
        deleteProduit(id, false);
    }

    @Override
    @Transactional
    public void deleteProduit(UUID id, boolean force) {
        com.gestiondestock.entity.Stock existingStock = stockRepository.findByProduitId(id);
        java.util.List<com.gestiondestock.entity.CommandeFournisseur> supplierOrders = commandeFournisseurRepository.findByProduitId(id);
        boolean hasSupplierOrders = !supplierOrders.isEmpty();
        boolean hasOrderLines = !ligneCommandeRepository.findByProduitId(id).isEmpty();

        if (!force && (hasSupplierOrders || hasOrderLines)) {
            throw new IllegalStateException("Impossible de supprimer: produit référencé par des commandes");
        }

        if (force && hasSupplierOrders) {
            for (com.gestiondestock.entity.CommandeFournisseur cf : supplierOrders) {
                java.util.List<com.gestiondestock.entity.EntreeStock> entrees = entreeStockRepository.findByCommandefournisseurId(cf.getId());
                if (entrees != null && !entrees.isEmpty()) {
                    for (com.gestiondestock.entity.EntreeStock e : entrees) {
                        e.setCommandefournisseur(null);
                    }
                    entreeStockRepository.saveAll(entrees);
                }
            }
            commandeFournisseurRepository.deleteAll(supplierOrders);
        }

        java.util.List<com.gestiondestock.entity.AlerteStock> alerts = alerteStockRepository.findByProduitId(id);
        if (!alerts.isEmpty()) {
            alerteStockRepository.deleteAll(alerts);
        }

        if (existingStock != null) {
            Integer qte = existingStock.getQuantiteDisponible();
            if (!force && qte != null && qte > 0) {
                throw new IllegalStateException("Impossible de supprimer: stock disponible > 0");
            }
            // Si force=true, on supprime le stock associé même si la quantité > 0
            stockRepository.delete(existingStock);
        }

        produitRepository.deleteById(id);
    }

    private void validateProduit(Produit produit) {
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new IllegalStateException("Le nom du produit est obligatoire");
        }
        if (produit.getPrixUnitaire() == null || produit.getPrixUnitaire() < 0) {
            throw new IllegalStateException("Le prix unitaire doit être >= 0");
        }
    }

    private void attachRelations(Produit produit) {
        produit.setCategorie(resolveCategorie(produit.getCategorie()));
        produit.setFournisseur(resolveFournisseur(produit.getFournisseur()));
    }

    private Categorie resolveCategorie(Categorie input) {
        if (input == null) return null;
        if (input.getId() != null) {
            return categorieRepository.findById(input.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        }
        if (input.getNom() != null && !input.getNom().trim().isEmpty()) {
            return categorieRepository.findFirstByNomIgnoreCase(input.getNom().trim())
                    .orElse(null);
        }
        return null;
    }

    private Fournisseur resolveFournisseur(Fournisseur input) {
        if (input == null) return null;
        if (input.getId() != null) {
            return fournisseurRepository.findById(input.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé"));
        }
        if (input.getNom() != null && !input.getNom().trim().isEmpty()) {
            return fournisseurRepository.findFirstByNomIgnoreCase(input.getNom().trim())
                    .orElse(null);
        }
        return null;
    }
}
