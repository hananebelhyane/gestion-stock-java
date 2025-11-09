package com.java.gestion_stock.repository;

import java.util.List;
import java.util.UUID;
import com.java.gestion_stock.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface ProduitRepository extends JpaRepository<Produit, UUID> {

    @Query("SELECT p FROM Produit p JOIN FETCH p.categorie JOIN FETCH p.fournisseur")
    List<Produit> findAllWithCategorieAndFournisseur();

    @Query("SELECT p FROM Produit p JOIN FETCH p.categorie JOIN FETCH p.fournisseur WHERE p.categorie.id = :categorieId")
    List<Produit> findByCategorieId(@Param("categorieId") UUID categorieId);

    @Query("SELECT p FROM Produit p JOIN FETCH p.categorie JOIN FETCH p.fournisseur WHERE p.fournisseur.id = :fournisseurId")
    List<Produit> findByFournisseurId(@Param("fournisseurId") UUID fournisseurId);
}
