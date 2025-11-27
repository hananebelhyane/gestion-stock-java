package com.gestiondestock.repository;

import com.gestiondestock.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    // Compter tous les clients
    long count();

    // Compter tous les clients actifs (non supprimés)
    @Query("SELECT COUNT(c) FROM Client c WHERE c.deleted_at IS NULL")
    long countClientsActifs();

    // Liste des Clients actifs seulement
    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NULL")
    List<Client> findClientsActifs();

    // Top clients par montant d'achats
    @Query("SELECT c.id, c.nom, c.prenom, SUM(lc.montantTotal) as totalAchats " +
            "FROM LigneCommande lc " +
            "JOIN lc.commande cc " +
            "JOIN cc.client c " +
            "WHERE cc.statut = 'confirmee' " +
            "GROUP BY c.id, c.nom, c.prenom " +
            "ORDER BY totalAchats DESC")
    List<Object[]> findTopClients();
}