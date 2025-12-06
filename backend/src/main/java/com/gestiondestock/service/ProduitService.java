package com.gestiondestock.service;

import com.gestiondestock.entity.Produit;

import java.util.List;
import java.util.UUID;

public interface ProduitService {

    Produit createProduit(Produit produit);

    Produit updateProduit(UUID id, Produit produit);

    Produit getProduitById(UUID id);

    List<Produit> getAllProduits();

    void deleteProduit(UUID id);
    void deleteProduit(UUID id, boolean force);
}
