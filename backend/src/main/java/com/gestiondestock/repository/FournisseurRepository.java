package com.gestiondestock.repository;

import com.gestiondestock.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, UUID> {
    
    // Rechercher un fournisseur par email
    Optional<Fournisseur> findByEmail(String email);
    
    // Rechercher un fournisseur par téléphone
    Optional<Fournisseur> findByTelephone(String telephone);
    
    // Récupérer tous les fournisseurs non supprimés
    @Query("SELECT f FROM Fournisseur f WHERE f.deleted_at IS NULL")
    List<Fournisseur> findAllActive();
    
    // Récupérer tous les fournisseurs supprimés
    @Query("SELECT f FROM Fournisseur f WHERE f.deleted_at IS NOT NULL")
    List<Fournisseur> findAllDeleted();
    
    // Rechercher par nom ou prénom (non supprimés)
    @Query("SELECT f FROM Fournisseur f WHERE f.deleted_at IS NULL AND (LOWER(f.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Fournisseur> searchByNomOrPrenom(String keyword);
    
    // Vérifier si un email existe déjà (pour les fournisseurs actifs)
    @Query("SELECT COUNT(f) > 0 FROM Fournisseur f WHERE f.email = :email AND f.deleted_at IS NULL")
    boolean existsByEmailAndNotDeleted(String email);
    
    // Vérifier si un téléphone existe déjà (pour les fournisseurs actifs)
    @Query("SELECT COUNT(f) > 0 FROM Fournisseur f WHERE f.telephone = :telephone AND f.deleted_at IS NULL")
    boolean existsByTelephoneAndNotDeleted(String telephone);
    Optional<Fournisseur> findFirstByNomIgnoreCase(String nom);
}
