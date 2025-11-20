package com.gestiondestock.controller;

import com.gestiondestock.entity.Client;
import com.gestiondestock.service.ClientService;
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
}
