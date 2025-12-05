package com.gestiondestock.controller;

import com.gestiondestock.entity.Client;
import com.gestiondestock.service.ClientService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
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
}
