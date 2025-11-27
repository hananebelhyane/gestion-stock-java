package com.gestiondestock.repository;

import com.gestiondestock.entity.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeClientRepository extends JpaRepository<CommandeClient, UUID> {

    //nombre total de commandes
    long count();//@Query("SELECT COUNT(*) FROM CommandeClient")


    long countByStatut(CommandeClient.StatutCommande statut);
    //« Compte le nombre de lignes dans la table CommandeClient dont la colonne statut a la valeur donnée. »

    // Commandes entre deux dates
    @Query("SELECT c FROM CommandeClient c WHERE c.dateCommande BETWEEN :debut AND :fin")
    List<CommandeClient> findCommandesParPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    //commandes du mois en cours
    @Query("SELECT c FROM CommandeClient c WHERE MONTH(c.dateCommande) = MONTH(CURRENT_DATE) " +
            "AND YEAR(c.dateCommande) = YEAR(CURRENT_DATE)")
    List<CommandeClient> findByDateCommandeDuMois();


    //TOUTES LES COMMANDES CONFIRMÉES
    List<CommandeClient> findByStatut(CommandeClient.StatutCommande statut);




}
