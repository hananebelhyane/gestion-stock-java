package com.gestiondestock.repository;

import com.gestiondestock.entity.AlerteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlerteStockRepository extends JpaRepository<AlerteStock, UUID> {

    // ========== MÉTHODES EXISTANTES (NE PAS TOUCHER) ==========
    
    // Alertes non lues (déjà existant)
    List<AlerteStock> findByStatut(AlerteStock.StatutAlerte statut);
    
    // Compter les alertes non lues (déjà existant)
    Long countByStatut(AlerteStock.StatutAlerte statut);

    // Dernières alertes (déjà existant)
    @Query("SELECT a FROM AlerteStock a ORDER BY a.dateAlerte DESC")
    List<AlerteStock> findDernieresAlertes();

    // Alertes par produit (déjà existant)
    List<AlerteStock> findByProduitId(UUID produitId);

    // ========== NOUVELLES MÉTHODES À AJOUTER ==========
    
    /**
     * Toutes les alertes non lues triées par gravité puis date
     */
    @Query("SELECT a FROM AlerteStock a WHERE a.statut = :statut " +
           "ORDER BY CASE a.niveauGravite " +
           "  WHEN 'CRITIQUE' THEN 1 " +
           "  WHEN 'MOYENNE' THEN 2 " +
           "  WHEN 'FAIBLE' THEN 3 " +
           "END, a.dateAlerte DESC")
    List<AlerteStock> findByStatutOrderByGravite(@Param("statut") AlerteStock.StatutAlerte statut);
    
    /**
     * Toutes les alertes triées par date décroissante
     */
    List<AlerteStock> findAllByOrderByDateAlerteDesc();
    
    /**
     * Alertes par niveau de gravité
     */
    @Query("SELECT a FROM AlerteStock a WHERE a.niveauGravite = :gravite " +
           "ORDER BY a.dateAlerte DESC")
    List<AlerteStock> findByNiveauGravite(@Param("gravite") AlerteStock.NiveauGravite gravite);
    
    /**
     * Compter par gravité et statut
     */
    Long countByNiveauGraviteAndStatut(
        AlerteStock.NiveauGravite gravite, 
        AlerteStock.StatutAlerte statut
    );

    // ========== REQUÊTES PAR PRODUIT ==========
    
    /**
     * Vérifier si une alerte non lue existe pour un produit
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM AlerteStock a " +
           "WHERE a.produit.id = :produitId " +
           "AND a.statut = 'NON_LU' " +
           "AND a.niveauGravite = :gravite")
    Boolean existsAlerteNonLuePourProduit(
        @Param("produitId") UUID produitId,
        @Param("gravite") AlerteStock.NiveauGravite gravite
    );

    // ========== REQUÊTES PAR DATE ==========
    
    /**
     * Alertes créées aujourd'hui
     */
    @Query("SELECT a FROM AlerteStock a " +
           "WHERE DATE(a.dateAlerte) = CURRENT_DATE " +
           "ORDER BY a.dateAlerte DESC")
    List<AlerteStock> findAlertesDuJour();
    
    /**
     * Alertes depuis une certaine date
     */
    @Query("SELECT a FROM AlerteStock a " +
           "WHERE a.dateAlerte >= :depuis " +
           "ORDER BY a.dateAlerte DESC")
    List<AlerteStock> findAlertesDepuis(@Param("depuis") LocalDateTime depuis);

    // ========== STATISTIQUES ==========
    
    /**
     * Statistiques par gravité (non lues seulement)
     */
    @Query("SELECT a.niveauGravite, COUNT(a) " +
           "FROM AlerteStock a " +
           "WHERE a.statut = 'NON_LU' " +
           "GROUP BY a.niveauGravite")
    List<Object[]> getStatistiquesParGravite();
}