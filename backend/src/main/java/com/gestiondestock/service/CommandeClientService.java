package com.gestiondestock.service;

import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.Client;
import com.gestiondestock.dto.CommandeClientRequest;
import com.gestiondestock.repository.CommandeClientRepository;
import com.gestiondestock.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommandeClientService {

    private final CommandeClientRepository repository;
    private final ClientRepository clientRepository;

    public CommandeClientService(CommandeClientRepository repository, ClientRepository clientRepository) {
        this.repository = repository;
        this.clientRepository = clientRepository;
    }

    public List<CommandeClient> findAll() {
        return repository.findAll();
    }

    public CommandeClient save(CommandeClient commande) {
        return repository.save(commande);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public CommandeClient createCommandeFromRequest(CommandeClientRequest request) {
        // Créer et persister le client d'abord
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setNom(request.getClient().getNom());
        client.setPrenom(request.getClient().getPrenom());
        client.setUsername(request.getClient().getNom().toLowerCase() + "." + request.getClient().getPrenom().toLowerCase());
        client.setTelephone(""); // valeur par défaut
        client.setAdresse(""); // valeur par défaut
        client.setMotDePasse(""); // valeur par défaut

        Client savedClient = clientRepository.save(client);

        // Créer la commande avec le client persisté
        CommandeClient commande = new CommandeClient();
        commande.setClient(savedClient);
        commande.setDateCommande(LocalDateTime.now());
        commande.setSeuilMax(request.getSeuilMax());

        // Gérer le statut
        if (request.getStatut() != null && !request.getStatut().isEmpty()) {
            try {
                commande.setStatut(CommandeClient.StatutCommande.valueOf(request.getStatut()));
            } catch (IllegalArgumentException e) {
                commande.setStatut(CommandeClient.StatutCommande.en_attente);
            }
        } else {
            commande.setStatut(CommandeClient.StatutCommande.en_attente);
        }

        return repository.save(commande);
    }
}
