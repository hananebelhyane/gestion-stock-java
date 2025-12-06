package com.gestiondestock.repository;

import com.gestiondestock.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByUsername(String username);

    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NULL")
    List<Client> findAllActive();

    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NOT NULL")
    List<Client> findAllDeleted();

    // Compter tous les clients
    long count();

    // Compter tous les clients actifs (non supprimés)
    @Query("SELECT COUNT(c) FROM Client c WHERE c.deleted_at IS NULL")
    long countClientsActifs();

    // Liste des Clients actifs seulement
    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NULL")
    List<Client> findClientsActifs();

    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NULL AND (LOWER(c.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')))" )
    List<Client> searchByNomOrPrenomOrUsername(String keyword);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c WHERE c.username = :username AND c.deleted_at IS NULL")
    boolean existsByUsernameAndNotDeleted(String username);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Client c WHERE c.telephone = :telephone AND c.deleted_at IS NULL")
    boolean existsByTelephoneAndNotDeleted(String telephone);

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
