package com.gestiondestock.repository;

import com.gestiondestock.entity.Magasinier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MagasinierRepository extends JpaRepository<Magasinier, UUID> {
    Optional<Magasinier> findByUsername(String username);
}
