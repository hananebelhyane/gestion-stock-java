package com.java.gestion_stock.service;

import com.java.gestion_stock.entity.Produit;
import com.java.gestion_stock.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class ProduitService {

    private final ProduitRepository repository;

    public ProduitService(ProduitRepository repository) {
        this.repository = repository;
    }

    public List<Produit> getAllProduits() {
        return repository.findAllWithCategorieAndFournisseur();
    }

    public Produit getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<Produit> getByCategorieId(UUID categorieId) {
        return repository.findByCategorieId(categorieId);
    }

    public List<Produit> getByFournisseurId(UUID fournisseurId) {
        return repository.findByFournisseurId(fournisseurId);
    }

    public Produit create(Produit p) {
        return repository.save(p);
    }

    public Produit update(UUID id, Produit p) {
        p.setId(id);
        return repository.save(p);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}