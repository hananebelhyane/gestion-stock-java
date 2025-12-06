package com.gestiondestock.repository;

import com.gestiondestock.entity.CommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, UUID> {

}
