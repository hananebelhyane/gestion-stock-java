package com.gestiondestock.repository;

import com.gestiondestock.entity.EntreeStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EntreeStockRepository extends JpaRepository<EntreeStock, UUID> {

    // ========== MÉTHODES EXISTANTES (de votre collègue) ==========
    
    // Les entrées récentes
    @Query("SELECT e FROM EntreeStock e ORDER BY e.date_entree DESC")
    List<EntreeStock> findEntreesRecentes();

    // Les entrées par période
    @Query("SELECT e FROM EntreeStock e WHERE e.date_entree BETWEEN :debut AND :fin")
    List<EntreeStock> findEntreesParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
    
    // ⚠️ IMPORTANT : Méthode utilisée par ProduitServiceImpl (ligne 111)
    // Ne pas supprimer cette méthode !
    List<EntreeStock> findByCommandefournisseurId(UUID commandeFournisseurId);
    
    // ========== VOS NOUVELLES MÉTHODES ==========
    
    // Trouver les entrées d'un produit spécifique
    @Query("SELECT e FROM EntreeStock e WHERE e.produit.id = :produitId ORDER BY e.date_entree DESC")
    List<EntreeStock> findByProduitId(@Param("produitId") UUID produitId);
    
    // Trouver les entrées d'un magasinier spécifique
    @Query("SELECT e FROM EntreeStock e WHERE e.magasinier.id = :magasinierId ORDER BY e.date_entree DESC")
    List<EntreeStock> findByMagasinierId(@Param("magasinierId") UUID magasinierId);
    
    // Entrées du jour
    @Query("SELECT e FROM EntreeStock e WHERE DATE(e.date_entree) = CURRENT_DATE ORDER BY e.date_entree DESC")
    List<EntreeStock> findEntreesDuJour();
    
    // Entrées avec JOIN FETCH pour éviter N+1
    @Query("SELECT e FROM EntreeStock e " +
           "LEFT JOIN FETCH e.produit " +
           "LEFT JOIN FETCH e.magasinier " +
           "LEFT JOIN FETCH e.commandefournisseur " +
           "ORDER BY e.date_entree DESC")
    List<EntreeStock> findAllWithDetails();
}