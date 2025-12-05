package com.gestiondestock.repository;

import com.gestiondestock.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, UUID> {

    // Nombre total de produits dans le stock
    long count();

    // Trouver les produits par catégorie
    List<Produit> findByCategorieId(UUID categorieId);

    // La valeur totale de stock: la somme totale de la valeur de tous les produits actuellement en stock
    @Query("SELECT SUM(s.produit.prixUnitaire * s.quantiteDisponible) FROM Stock s")
    Double calculerValeurTotaleStock();
}
