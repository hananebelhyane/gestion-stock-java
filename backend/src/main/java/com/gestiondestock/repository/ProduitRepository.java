package com.gestiondestock.repository;

import com.gestiondestock.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, UUID> {

    // Trouver un produit par nom (utile pour éviter les doublons)
    Produit findByNom(String nom);

    // Chercher par nom contenant (utile pour recherche dans interface JavaFX)
    List<Produit> findByNomContainingIgnoreCase(String nom);

    // Chercher tous les produits d'une catégorie
    List<Produit> findByCategorieId(UUID categorieId);

    // Chercher tous les produits d'un fournisseur
    List<Produit> findByFournisseurId(UUID fournisseurId);
}
