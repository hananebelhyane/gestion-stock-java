package com.gestiondestock.service;

import com.gestiondestock.entity.Produit;
import com.gestiondestock.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @Override
    public Produit createProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    @Override
    public Produit updateProduit(UUID id, Produit produit) {
        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        existing.setNom(produit.getNom());
        existing.setDescription(produit.getDescription());
        existing.setPrixUnitaire(produit.getPrixUnitaire());
        existing.setUrlImage(produit.getUrlImage());
        existing.setCategorie(produit.getCategorie());
        existing.setFournisseur(produit.getFournisseur());

        return produitRepository.save(existing);
    }

    @Override
    public Produit getProduitById(UUID id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
    }

    @Override
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    @Override
    public void deleteProduit(UUID id) {
        produitRepository.deleteById(id);
    }
}
