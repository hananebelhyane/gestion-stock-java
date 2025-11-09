package com.java.gestion_stock.controller;

import com.java.gestion_stock.entity.Produit;
import com.java.gestion_stock.service.ProduitService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produit")

public class ProduitController {
    private final ProduitService service;

    public ProduitController(ProduitService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produit> getAllProduits() {
        return service.getAllProduits();
    }

    @GetMapping("/{Id}")
    public Produit getById(@PathVariable UUID Id) {
        return service.getById(Id);
    }

    @GetMapping("/categorie/{categorieId}")
    public List<Produit> getAllProduitsCategories(@PathVariable UUID categorieId) {
        return service.getByCategorieId(categorieId);
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    public List<Produit> getAllProduitsFournisseur(@PathVariable UUID fournisseurId) {
        return service.getByFournisseurId(fournisseurId);
    }

    @PostMapping
    public Produit addProduit(@RequestBody Produit produit) {
        return service.create(produit);
    }

    @DeleteMapping("/{Id}")
    public void deleteProduit(@PathVariable UUID Id) {
        service.delete(Id);
    }
}
