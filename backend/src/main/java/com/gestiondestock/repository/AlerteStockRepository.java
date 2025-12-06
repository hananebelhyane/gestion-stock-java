package com.gestiondestock.repository;

import com.gestiondestock.entity.AlerteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlerteStockRepository extends JpaRepository<AlerteStock, UUID> {

        //Alertes non lues
    List<AlerteStock> findByStatut(AlerteStock.StatutAlerte statut);
    //EXEMPLE D UTILISATION:
    //List<AlerteStock> alertesNonLues = alertesStockRepository.findByStatut(AlertesStock.StatutAlerte.NON_LU);


    //COMPTER LES ALERTES NON LUES:
    Long countByStatut(AlerteStock.StatutAlerte statut);

    // dernieres alertes
    @Query("SELECT a FROM AlerteStock a ORDER BY a.dateAlerte DESC")
    List<AlerteStock> findDernieresAlertes();

    java.util.List<AlerteStock> findByProduitId(UUID produitId);

}
