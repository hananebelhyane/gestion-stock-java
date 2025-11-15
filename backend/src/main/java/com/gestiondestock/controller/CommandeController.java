package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.service.CommandeClientService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeClientService commandeService;

    public CommandeController(CommandeClientService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping("/clients")
    public List<CommandeClient> getAllCommandesClient() {
        return commandeService.findAll();
    }
}
