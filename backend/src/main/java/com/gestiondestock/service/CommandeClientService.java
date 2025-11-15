package com.gestiondestock.service;

import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.repository.CommandeClientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommandeClientService {

    private final CommandeClientRepository repository;

    public CommandeClientService(CommandeClientRepository repository) {
        this.repository = repository;
    }

    public List<CommandeClient> findAll() {
        return repository.findAll();
    }
}
