package com.gestiondestock.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestiondestock.entity.Panier;

public interface PanierRepository extends JpaRepository<Panier, UUID> {
    Optional<Panier> findByClientId(UUID clientId);
}
