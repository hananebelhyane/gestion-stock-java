package com.gestiondestock.service;

import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.repository.CommandeFournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommandeFournisseurService {

    @Autowired
    private CommandeFournisseurRepository commandeFournisseurRepository;

    public List<CommandeFournisseur> findAll() {
        return commandeFournisseurRepository.findAll();
    }

    public CommandeFournisseur save(CommandeFournisseur commande) {
        // JPA cascade PERSIST gérera automatiquement la création du produit
        return commandeFournisseurRepository.save(commande);
    }

    public void deleteById(UUID id) {
        commandeFournisseurRepository.deleteById(id);
    }
}
