package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.dto.CommandeFournisseurRequest;
import com.gestiondestock.repository.CommandeFournisseurRepository;
import com.gestiondestock.service.CommandeFournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commandes")
public class CommandeFournisseurController {

    private final CommandeFournisseurRepository repository;
    private final CommandeFournisseurService service;

    public CommandeFournisseurController(CommandeFournisseurRepository repository, CommandeFournisseurService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/fournisseurs")
    public ResponseEntity<?> getAllCommandesFournisseur() {
        try {
            List<CommandeFournisseur> commandes = repository.findAll();
            return ResponseEntity.ok(commandes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du chargement: " + e.getMessage());
        }
    }

    @PostMapping("/fournisseurs")
    public ResponseEntity<?> createCommandeFournisseur(@RequestBody CommandeFournisseurRequest request) {
        try {
            System.out.println("📥 Réception commande fournisseur");

            // Créer le produit avec un ID généré
            Produit produit = new Produit();
            produit.setId(UUID.randomUUID());
            produit.setNom(request.getProduit().getNom());
            produit.setDescription(request.getProduit().getDescription());

            // Créer la commande fournisseur
            CommandeFournisseur commande = new CommandeFournisseur();
            commande.setId(UUID.randomUUID());
            commande.setProduit(produit);
            commande.setCommandeDate(LocalDateTime.now());

            // Gérer le statut
            if (request.getStatut() != null && !request.getStatut().isEmpty()) {
                try {
                    commande.setStatut(CommandeFournisseur.StatutCommande.valueOf(request.getStatut()));
                } catch (IllegalArgumentException e) {
                    commande.setStatut(CommandeFournisseur.StatutCommande.en_attente);
                }
            } else {
                commande.setStatut(CommandeFournisseur.StatutCommande.en_attente);
            }

            CommandeFournisseur saved = service.save(commande);
            System.out.println("✅ Commande sauvegardée");
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de la création: " + e.getMessage());
        }
    }

    @DeleteMapping("/fournisseurs/{id}")
    public ResponseEntity<Void> deleteCommandeFournisseur(@PathVariable String id) {
        service.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
