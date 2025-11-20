package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.service.CommandeClientService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CommandeClient> createCommandeClient(@RequestBody CommandeClient commande) {
        CommandeClient saved = commandeService.save(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteCommandeClient(@PathVariable String id) {
        commandeService.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
