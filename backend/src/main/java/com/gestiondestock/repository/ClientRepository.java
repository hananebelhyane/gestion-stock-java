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
    
    // Rechercher un client par username
    Optional<Client> findByUsername(String username);
    
    // Rechercher un client par téléphone
    Optional<Client> findByTelephone(String telephone);
    
    // Récupérer tous les clients non supprimés
    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NULL")
    List<Client> findAllActive();
    
    // Récupérer tous les clients supprimés
    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NOT NULL")
    List<Client> findAllDeleted();
    
    // Rechercher par nom ou prénom (non supprimés)
    @Query("SELECT c FROM Client c WHERE c.deleted_at IS NULL AND (LOWER(c.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Client> searchByNomOrPrenomOrUsername(String keyword);
    
    // Vérifier si un username existe déjà (pour les clients actifs)
    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE c.username = :username AND c.deleted_at IS NULL")
    boolean existsByUsernameAndNotDeleted(String username);
    
    // Vérifier si un téléphone existe déjà (pour les clients actifs)
    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE c.telephone = :telephone AND c.deleted_at IS NULL")
    boolean existsByTelephoneAndNotDeleted(String telephone);
}