package com.gestiondestock.repository;

import com.gestiondestock.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FactureRepository extends JpaRepository<Facture, UUID> {

    // Récupérer facture par commande
    @Query("SELECT f FROM Facture f WHERE f.commande.id = :commandeId")
    Optional<Facture> findByCommandeId(@Param("commandeId") UUID commandeId);

    // Toutes les factures d'un client
    @Query("SELECT f FROM Facture f WHERE f.commande.client.id = :clientId ORDER BY f.dateFacture DESC")
    List<Facture> findByClientId(@Param("clientId") UUID clientId);

    // Factures par période
    @Query("SELECT f FROM Facture f WHERE f.dateFacture BETWEEN :debut AND :fin ORDER BY f.dateFacture DESC")
    List<Facture> findByDateRange(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    // Factures non payées
    @Query("SELECT f FROM Facture f WHERE f.estPayee = false ORDER BY f.dateFacture DESC")
    List<Facture> findUnpaidFactures();
}
