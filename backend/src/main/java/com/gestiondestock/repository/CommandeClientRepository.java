package com.gestiondestock.repository;

import com.gestiondestock.entity.CommandeClient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface CommandeClientRepository extends JpaRepository<CommandeClient, UUID> {
}
