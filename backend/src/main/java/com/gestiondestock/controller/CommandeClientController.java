package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.Client;
import com.gestiondestock.dto.CommandeClientRequest;
import com.gestiondestock.dto.CommandeClientResponse;
import com.gestiondestock.service.CommandeClientService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/commandes")
@PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')") 
public class CommandeClientController {

    private final CommandeClientService commandeService;

    public CommandeClientController(CommandeClientService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping("/clients")
    public List<CommandeClientResponse> getAllCommandesClient() {
        return commandeService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/clients")
    public ResponseEntity<CommandeClientResponse> createCommandeClient(@RequestBody CommandeClientRequest request) {
        try {
            CommandeClient saved = commandeService.createCommandeFromRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur création commande: " + e.getMessage());
        }
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteCommandeClient(@PathVariable String id) {
        commandeService.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    private CommandeClientResponse toResponse(CommandeClient commande) {
        Client c = commande.getClient();
        CommandeClientResponse.ClientInfo clientInfo = null;
        if (c != null) {
            clientInfo = new CommandeClientResponse.ClientInfo(
                    c.getId(),
                    c.getNom(),
                    c.getPrenom(),
                    c.getUsername(),
                    c.getTelephone(),
                    c.getAdresse()
            );
        }
        return new CommandeClientResponse(
                commande.getId(),
                clientInfo,
                commande.getDateCommande(),
                commande.getStatut() != null ? commande.getStatut().name() : "en_attente",
                commande.getSeuilMax()
        );
    }
}
