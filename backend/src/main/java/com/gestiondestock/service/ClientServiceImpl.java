package com.gestiondestock.service;

import com.gestiondestock.entity.Client;
import com.gestiondestock.repository.ClientRepository;
import com.gestiondestock.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Client registerClient(Client client) {
        if (client.getMotDePasse() != null) {
            client.setMotDePasse(passwordEncoder.encode(client.getMotDePasse()));
        }
        return clientRepository.save(client);
    }

    @Override
    public java.util.List<Client> getAllActiveClients() {
        return clientRepository.findAllActive();
    }

    @Override
    @Transactional
    public Client createClient(Client client) {
        if (client.getMotDePasse() != null) {
            client.setMotDePasse(passwordEncoder.encode(client.getMotDePasse()));
        }
        // Unicité sur username/telephone pour les clients actifs
        if (client.getUsername() != null && clientRepository.existsByUsernameAndNotDeleted(client.getUsername())) {
            throw new IllegalStateException("Un client avec ce username existe déjà");
        }
        if (client.getTelephone() != null && clientRepository.existsByTelephoneAndNotDeleted(client.getTelephone())) {
            throw new IllegalStateException("Un client avec ce téléphone existe déjà");
        }
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client updateClient(java.util.UUID id, Client input) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        if (existing.getDeleted_at() != null) {
            throw new IllegalStateException("Impossible de modifier un client supprimé");
        }
        // Unicité si changements
        if (input.getUsername() != null && !input.getUsername().equals(existing.getUsername())
                && clientRepository.existsByUsernameAndNotDeleted(input.getUsername())) {
            throw new IllegalStateException("Un client avec ce username existe déjà");
        }
        if (input.getTelephone() != null && !input.getTelephone().equals(existing.getTelephone())
                && clientRepository.existsByTelephoneAndNotDeleted(input.getTelephone())) {
            throw new IllegalStateException("Un client avec ce téléphone existe déjà");
        }
        existing.setNom(input.getNom());
        existing.setPrenom(input.getPrenom());
        existing.setUsername(input.getUsername());
        existing.setTelephone(input.getTelephone());
        existing.setAdresse(input.getAdresse());
        if (input.getMotDePasse() != null && !input.getMotDePasse().isBlank()) {
            existing.setMotDePasse(passwordEncoder.encode(input.getMotDePasse()));
        }
        return clientRepository.save(existing);
    }

    @Override
    public Client getClientById(java.util.UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
    }

    @Override
    @Transactional
    public void deleteClient(java.util.UUID id, java.util.UUID deletedBy) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        if (client.getDeleted_at() != null) {
            throw new IllegalStateException("Ce client est déjà supprimé");
        }
        client.setDeleted_at(java.time.LocalDateTime.now());
        client.setDeleted_by(deletedBy);
        clientRepository.save(client);
    }

    @Override
    public java.util.List<Client> searchClients(String keyword) {
        return clientRepository.searchByNomOrPrenomOrUsername(keyword);
    }

    @Override
    public java.util.List<Client> getDeletedClients() {
        return clientRepository.findAllDeleted();
    }

    @Override
    @Transactional
    public Client restoreClient(java.util.UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        if (client.getDeleted_at() == null) {
            throw new IllegalStateException("Ce client n'est pas supprimé");
        }
        client.setDeleted_at(null);
        client.setDeleted_by(null);
        return clientRepository.save(client);
    }
}
