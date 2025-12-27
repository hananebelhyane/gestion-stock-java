package com.gestiondestock.controller;

import com.gestiondestock.entity.Categorie;
import com.gestiondestock.service.CategorieService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    public Categorie create(@RequestBody Categorie categorie) {
        return categorieService.createCategorie(categorie);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    public Categorie update(@PathVariable UUID id, @RequestBody Categorie categorie) {
        return categorieService.updateCategorie(id, categorie);
    }

    @GetMapping
    public List<Categorie> findAll() {
        return categorieService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Categorie findOne(@PathVariable UUID id) {
        return categorieService.getCategorie(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    public void delete(@PathVariable UUID id) {
        categorieService.deleteCategorie(id);
    }
}
