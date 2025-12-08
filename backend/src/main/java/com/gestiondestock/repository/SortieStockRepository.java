package com.gestiondestock.repository;

import com.gestiondestock.entity.SortieStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SortieStockRepository extends JpaRepository<SortieStock, UUID> {

    // Les sorties récentes (déjà existant)
    @Query("SELECT s FROM SortieStock s ORDER BY s.date_sortie DESC")
    List<SortieStock> findSortiesRecentes();

    // Les sorties par période (déjà existant)
    @Query("SELECT s FROM SortieStock s WHERE s.date_sortie BETWEEN :debut AND :fin")
    List<SortieStock> findSortiesParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
    
    // ========== NOUVELLES MÉTHODES À AJOUTER ==========
    
    // Trouver les sorties d'un produit spécifique
    @Query("SELECT s FROM SortieStock s WHERE s.produit.id = :produitId ORDER BY s.date_sortie DESC")
    List<SortieStock> findByProduitId(@Param("produitId") UUID produitId);
    
    // Trouver les sorties d'un magasinier spécifique
    @Query("SELECT s FROM SortieStock s WHERE s.magasinier.id = :magasinierId ORDER BY s.date_sortie DESC")
    List<SortieStock> findByMagasinierId(@Param("magasinierId") UUID magasinierId);
    
    // Sorties du jour
    @Query("SELECT s FROM SortieStock s WHERE DATE(s.date_sortie) = CURRENT_DATE ORDER BY s.date_sortie DESC")
    List<SortieStock> findSortiesDuJour();
    
    // Sorties avec JOIN FETCH pour éviter N+1
    @Query("SELECT s FROM SortieStock s " +
           "LEFT JOIN FETCH s.produit " +
           "LEFT JOIN FETCH s.magasinier " +
           "LEFT JOIN FETCH s.lignecommande " +
           "ORDER BY s.date_sortie DESC")
    List<SortieStock> findAllWithDetails();
}