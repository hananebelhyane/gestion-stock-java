package com.gestiondestock.service;

import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.repository.CommandeFournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeFournisseurService {

    @Autowired
    private CommandeFournisseurRepository commandeFournisseurRepository;

    public List<CommandeFournisseur> findAll() {
        return commandeFournisseurRepository.findAll();
    }
}
