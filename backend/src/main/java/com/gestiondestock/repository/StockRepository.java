package com.gestiondestock.repository;

import java.util.List;
import java.util.UUID;

import com.gestiondestock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    @Query("SELECT s FROM Stock s JOIN FETCH s.produit")
    List<Stock> findAllWithProduit();

    @Query("SELECT s FROM Stock s WHERE s.produit.id = :produitId")
    Stock findByProduitId(@Param("produitId") UUID produitId);

}
