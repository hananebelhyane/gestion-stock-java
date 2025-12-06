package com.gestiondestock.repository;

import com.gestiondestock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    // les produits en rupture de stock
    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible = 0")
    List<Stock> findProduitsEnRupture();

    // les produits avec un stock faible
    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible <= s.seuilAlerte AND s.quantiteDisponible > 0")
    List<Stock> findProduitsStockFaible();

    // le nombre des produits en rupture
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.quantiteDisponible = 0")
    long countProduitsEnRupture();

    // le nombre des produits avec stock faible
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.quantiteDisponible <= s.seuilAlerte AND s.quantiteDisponible > 0")
    long countProduitsFaible();

    // tous les stocks avec leurs produits (JOIN FETCH pour éviter N+1)
    @Query("SELECT s FROM Stock s JOIN FETCH s.produit")
    List<Stock> findAllWithProduit();

    // trouver le stock d'un produit par son ID
    @Query("SELECT s FROM Stock s WHERE s.produit.id = :produitId")
    Stock findByProduitId(@Param("produitId") UUID produitId);
}
