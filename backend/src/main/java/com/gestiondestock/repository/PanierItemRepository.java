package com.gestiondestock.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestiondestock.entity.PanierItem;

public interface PanierItemRepository extends JpaRepository<PanierItem, UUID> {
}
