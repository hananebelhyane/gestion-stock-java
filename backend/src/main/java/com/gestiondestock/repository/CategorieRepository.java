package com.gestiondestock.repository;

import com.gestiondestock.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, UUID> {

    //COMPTER LES CATEGORIES
    long count();
    java.util.Optional<Categorie> findFirstByNomIgnoreCase(String nom);
}
