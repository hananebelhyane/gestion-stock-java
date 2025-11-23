package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.Client;
import com.gestiondestock.dto.CommandeClientRequest;
import com.gestiondestock.service.CommandeClientService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commandes")
public class CommandeClientController {

    private final CommandeClientService commandeService;

    public CommandeClientController(CommandeClientService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping("/clients")
    public List<CommandeClient> getAllCommandesClient() {
        return commandeService.findAll();
    }

    @PostMapping("/clients")
    public ResponseEntity<CommandeClient> createCommandeClient(@RequestBody CommandeClientRequest request) {
        // Créer le client avec un ID généré
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setNom(request.getClient().getNom());
        client.setPrenom(request.getClient().getPrenom());
        client.setUsername(request.getClient().getNom().toLowerCase() + "." + request.getClient().getPrenom().toLowerCase());

        // Créer la commande
        CommandeClient commande = new CommandeClient();
        commande.setClient(client);
        commande.setDateCommande(LocalDateTime.now());
        commande.setSeuilMax(request.getSeuilMax());

        // Gérer le statut
        if (request.getStatut() != null && !request.getStatut().isEmpty()) {
            try {
                commande.setStatut(CommandeClient.StatutCommande.valueOf(request.getStatut()));
            } catch (IllegalArgumentException e) {
                commande.setStatut(CommandeClient.StatutCommande.en_attente);
            }
        } else {
            commande.setStatut(CommandeClient.StatutCommande.en_attente);
        }

        CommandeClient saved = commandeService.save(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteCommandeClient(@PathVariable String id) {
        commandeService.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
