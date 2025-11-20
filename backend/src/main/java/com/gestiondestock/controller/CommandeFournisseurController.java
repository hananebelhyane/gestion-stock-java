package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.repository.CommandeFournisseurRepository;
import com.gestiondestock.service.CommandeFournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createCommandeFournisseur(@RequestBody CommandeFournisseur commande) {
        try {
            System.out.println("📥 Réception commande fournisseur");
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
