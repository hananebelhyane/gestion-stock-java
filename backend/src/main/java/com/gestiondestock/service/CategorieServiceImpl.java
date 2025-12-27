package com.gestiondestock.service;

import com.gestiondestock.entity.Categorie;
import com.gestiondestock.exception.ResourceNotFoundException;
import com.gestiondestock.repository.CategorieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieServiceImpl(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    @Transactional
    public Categorie createCategorie(Categorie categorie) {
        validateCategorie(categorie);
        categorie.setId(null);
        return categorieRepository.save(categorie);
    }

    @Override
    @Transactional
    public Categorie updateCategorie(UUID id, Categorie categorie) {
        Categorie existing = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        validateCategorie(categorie);
        existing.setNom(categorie.getNom());
        existing.setDescription(categorie.getDescription());
        return categorieRepository.save(existing);
    }

    @Override
    public Categorie getCategorie(UUID id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
    }

    @Override
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteCategorie(UUID id) {
        categorieRepository.deleteById(id);
    }

    private void validateCategorie(Categorie categorie) {
        if (categorie == null || categorie.getNom() == null || categorie.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie est obligatoire");
        }
    }
}
