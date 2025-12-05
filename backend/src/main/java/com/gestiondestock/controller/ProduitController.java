package com.gestiondestock.controller;

import com.gestiondestock.entity.Produit;
import com.gestiondestock.service.ProduitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin("*")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @PostMapping
    public Produit createProduit(@RequestBody Produit produit) {
        return produitService.createProduit(produit);
    }

    @GetMapping("/{id}")
    public Produit getProduit(@PathVariable UUID id) {
        return produitService.getProduitById(id);
    }

    @GetMapping
    public List<Produit> getAllProduits() {
        return produitService.getAllProduits();
    }

    @PutMapping("/{id}")
    public Produit updateProduit(@PathVariable UUID id, @RequestBody Produit produit) {
        return produitService.updateProduit(id, produit);
    }

    @DeleteMapping("/{id}")
    public void deleteProduit(@PathVariable UUID id) {
        produitService.deleteProduit(id);
    }
}
