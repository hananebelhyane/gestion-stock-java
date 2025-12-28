package com.gestiondestock.service;

import com.gestiondestock.entity.Categorie;
import java.util.List;
import java.util.UUID;

public interface CategorieService {
    Categorie createCategorie(Categorie categorie);

    Categorie updateCategorie(UUID id, Categorie categorie);

    Categorie getCategorie(UUID id);

    List<Categorie> getAllCategories();

    void deleteCategorie(UUID id);
}
