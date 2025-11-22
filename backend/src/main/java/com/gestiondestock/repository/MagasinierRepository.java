package com.gestiondestock.repository;

import com.gestiondestock.entity.Magasinier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MagasinierRepository extends JpaRepository<Magasinier, UUID> {
    
    // Rechercher un magasinier par username
    Optional<Magasinier> findByUsername(String username);
    
    // Rechercher un magasinier par téléphone
    Optional<Magasinier> findByTelephone(String telephone);
    
    // Récupérer tous les magasiniers non supprimés
    @Query("SELECT m FROM Magasinier m WHERE m.deleted_at IS NULL")
    List<Magasinier> findAllActive();
    
    // Récupérer tous les magasiniers supprimés
    @Query("SELECT m FROM Magasinier m WHERE m.deleted_at IS NOT NULL")
    List<Magasinier> findAllDeleted();
    
    // Rechercher par nom ou prénom (non supprimés)
    @Query("SELECT m FROM Magasinier m WHERE m.deleted_at IS NULL AND (LOWER(m.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.username) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Magasinier> searchByNomOrPrenomOrUsername(String keyword);
    
    // Vérifier si un username existe déjà (pour les magasiniers actifs)
    @Query("SELECT COUNT(m) > 0 FROM Magasinier m WHERE m.username = :username AND m.deleted_at IS NULL")
    boolean existsByUsernameAndNotDeleted(String username);
    
    // Vérifier si un téléphone existe déjà (pour les magasiniers actifs)
    @Query("SELECT COUNT(m) > 0 FROM Magasinier m WHERE m.telephone = :telephone AND m.deleted_at IS NULL")
    boolean existsByTelephoneAndNotDeleted(String telephone);
}