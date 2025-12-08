
package com.gestiondestock.controller;

import com.gestiondestock.entity.Produit;
import com.gestiondestock.service.ProduitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin("*")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')") 
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')") 
    public Produit updateProduit(@PathVariable UUID id, @RequestBody Produit produit) {
        return produitService.updateProduit(id, produit);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')") 
    public void deleteProduit(@PathVariable UUID id, @RequestParam(name = "force", required = false, defaultValue = "false") boolean force) {
        produitService.deleteProduit(id, force);
    }
}
