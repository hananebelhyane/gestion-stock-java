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
}
