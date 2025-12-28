package com.gestiondestock.controller;

import com.gestiondestock.entity.Client;
import com.gestiondestock.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/register")
    public Client registerClient(@RequestBody Client client) {
        return clientService.registerClient(client);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClient(@RequestBody Client client) {
        Client saved = clientService.createClient(client);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("data", saved);
        return ResponseEntity.status(201).body(body);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActiveClients() {
        try {
            java.util.List<com.gestiondestock.entity.Client> clients = clientService.getAllActiveClients();
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("data", clients);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            // Retourner une réponse 200 avec data = [] pour éviter un crash côté frontend
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("data", java.util.Collections.emptyList());
            body.put("error", e.getMessage());
            return ResponseEntity.ok(body);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable java.util.UUID id) {
        Client client = clientService.getClientById(id);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("data", client);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable java.util.UUID id, @RequestBody Client client) {
        Client updated = clientService.updateClient(id, client);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("data", updated);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClient(@PathVariable java.util.UUID id,
            @RequestParam(required = false) java.util.UUID deleted_by) {
        java.util.UUID userId = deleted_by != null ? deleted_by : java.util.UUID.randomUUID();
        clientService.deleteClient(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchClients(@RequestParam String keyword) {
        java.util.List<Client> clients = clientService.searchClients(keyword);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("data", clients);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedClients() {
        java.util.List<Client> clients = clientService.getDeletedClients();
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("data", clients);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreClient(@PathVariable java.util.UUID id) {
        Client restored = clientService.restoreClient(id);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("data", restored);
        return ResponseEntity.ok(body);
    }
}
