package com.gestiondestock.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestiondestock.entity.Panier;
import com.gestiondestock.service.PanierService;

@RestController
@RequestMapping("/api/panier")
@CrossOrigin(origins = "*")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @GetMapping("/{clientId}")
    public Panier getPanier(@PathVariable UUID clientId) {
        return panierService.getOrCreatePanier(clientId);
    }

    @PostMapping("/ajouter")
    public Panier ajouterProduit(
            @RequestParam UUID clientId,
            @RequestParam UUID produitId,
            @RequestParam int quantite) {
        return panierService.ajouterProduit(clientId, produitId, quantite);
    }

    @PutMapping("/modifier")
    public void modifierQuantite(
            @RequestParam UUID itemId,
            @RequestParam int quantite) {
        panierService.modifierQuantite(itemId, quantite);
    }

    @DeleteMapping("/supprimer/{itemId}")
    public void supprimerItem(@PathVariable UUID itemId) {
        panierService.supprimerItem(itemId);
    }

    @DeleteMapping("/vider/{clientId}")
    public void viderPanier(@PathVariable UUID clientId) {
        panierService.viderPanier(clientId);
    }
}
