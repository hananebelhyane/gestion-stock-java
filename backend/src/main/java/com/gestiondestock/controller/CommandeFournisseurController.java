package com.gestiondestock.controller;

import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.repository.CommandeFournisseurRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeFournisseurController {

    private final CommandeFournisseurRepository repository;

    public CommandeFournisseurController(CommandeFournisseurRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/fournisseurs")
    public List<CommandeFournisseur> getAllCommandesFournisseur() {
        return repository.findAll();
    }
}
