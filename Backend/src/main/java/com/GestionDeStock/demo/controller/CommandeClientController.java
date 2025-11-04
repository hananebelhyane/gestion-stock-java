package com.GestionDeStock.demo.controller;

import com.GestionDeStock.demo.model.CommandeClient;
import com.GestionDeStock.demo.service.CommandeClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/client")
@CrossOrigin(origins = "*")
public class CommandeClientController {
    
    @Autowired
    private CommandeClientService commandeClientService;
    
    // GET /api/commandes/client - Liste toutes les commandes
    @GetMapping
    public List<CommandeClient> getAllCommandes() {
        return commandeClientService.getAllCommandes();
    }
    
    // GET /api/commandes/client/{id} - Une commande par ID
    @GetMapping("/{id}")
    public ResponseEntity<CommandeClient> getCommandeById(@PathVariable Integer id) {
        return commandeClientService.getCommandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/commandes/client - Créer une commande
    @PostMapping
    public CommandeClient creerCommande(@RequestBody CommandeClient commande) {
        return commandeClientService.creerCommande(commande);
    }
    
    // PUT /api/commandes/client/{id} - Modifier une commande
    @PutMapping("/{id}")
    public ResponseEntity<CommandeClient> modifierCommande(
            @PathVariable Integer id,
            @RequestBody CommandeClient commande) {
        return ResponseEntity.ok(commandeClientService.modifierCommande(id, commande));
    }
    
    // DELETE /api/commandes/client/{id} - Supprimer une commande
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommande(@PathVariable Integer id) {
        commandeClientService.supprimerCommande(id);
        return ResponseEntity.noContent().build();
    }
    
    // GET /api/commandes/client/statut/{statut} - Par statut
    @GetMapping("/statut/{statut}")
    public List<CommandeClient> getCommandesByStatut(@PathVariable String statut) {
        return commandeClientService.getCommandesByStatut(statut);
    }
}