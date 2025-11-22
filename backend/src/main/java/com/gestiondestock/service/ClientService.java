package com.gestiondestock.service;


import com.gestiondestock.dto.ClientDTO;
import com.gestiondestock.dto.ClientRequestDTO;
import com.gestiondestock.entity.Client;
import com.gestiondestock.exception.ResourceNotFoundException;
import com.gestiondestock.exception.DuplicateResourceException;
import com.gestiondestock.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Créer un nouveau client
     */
    public ClientDTO createClient(ClientRequestDTO request) {
        log.info("Création d'un nouveau client avec username: {}", request.getUsername());

        if (clientRepository.existsByUsernameAndNotDeleted(request.getUsername())) {
            throw new DuplicateResourceException("Un client avec ce username existe déjà");
        }

        if (clientRepository.existsByTelephoneAndNotDeleted(request.getTelephone())) {
            throw new DuplicateResourceException("Un client avec ce téléphone existe déjà");
        }

        Client client = new Client();
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setUsername(request.getUsername());
        client.setTelephone(request.getTelephone());
        client.setMotDePasse(request.getMotDePasse());
        client.setAdresse(request.getAdresse());

        Client savedClient = clientRepository.save(client);
        log.info("Client créé avec succès avec ID: {}", savedClient.getId());

        return convertToDTO(savedClient);
    }
    // --- PUBLIC : inscription
    public Client registerClient(Client client) {
        log.info("Inscription d'un nouveau client avec username: {}", client.getUsername());

        if (clientRepository.existsByUsernameAndNotDeleted(client.getUsername())) {
            throw new DuplicateResourceException("Un client avec ce username existe déjà");
        }
        if (clientRepository.existsByTelephoneAndNotDeleted(client.getTelephone())) {
            throw new DuplicateResourceException("Un client avec ce téléphone existe déjà");
        }

        // Encodage mot de passe
        if (client.getMotDePasse() != null) {
            client.setMotDePasse(passwordEncoder.encode(client.getMotDePasse()));
        }

        return clientRepository.save(client);
    }
    

    /**
     * Récupérer tous les clients actifs
     */
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllActiveClients() {
        log.info("Récupération de tous les clients actifs");
        return clientRepository.findAllActive()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les clients supprimés
     */
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllDeletedClients() {
        log.info("Récupération de tous les clients supprimés");
        return clientRepository.findAllDeleted()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un client par ID
     */
    @Transactional(readOnly = true)
    public ClientDTO getClientById(UUID id) {
        log.info("Récupération du client avec ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));
        return convertToDTO(client);
    }

    /**
     * Mettre à jour un client
     */
    public ClientDTO updateClient(UUID id, ClientRequestDTO request) {
        log.info("Mise à jour du client avec ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));

        if (client.getDeleted_at() != null) {
            throw new IllegalStateException("Impossible de modifier un client supprimé");
        }

        if (!client.getUsername().equals(request.getUsername()) &&
                clientRepository.existsByUsernameAndNotDeleted(request.getUsername())) {
            throw new DuplicateResourceException("Un client avec ce username existe déjà");
        }

        if (!client.getTelephone().equals(request.getTelephone()) &&
                clientRepository.existsByTelephoneAndNotDeleted(request.getTelephone())) {
            throw new DuplicateResourceException("Un client avec ce téléphone existe déjà");
        }

        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setUsername(request.getUsername());
        client.setTelephone(request.getTelephone());
        client.setMotDePasse(request.getMotDePasse());
        client.setAdresse(request.getAdresse());

        Client updatedClient = clientRepository.save(client);
        log.info("Client mis à jour avec succès");

        return convertToDTO(updatedClient);
    }

    /**
     * Supprimer un client (soft delete)
     */
    public void deleteClient(UUID id, UUID deletedBy) {
        log.info("Suppression du client avec ID: {} par l'utilisateur: {}", id, deletedBy);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));

        if (client.getDeleted_at() != null) {
            throw new IllegalStateException("Ce client est déjà supprimé");
        }

        client.setDeleted_at(LocalDateTime.now());
        client.setDeleted_by(deletedBy);
        clientRepository.save(client);

        log.info("Client supprimé avec succès");
    }

    /**
     * Restaurer un client supprimé
     */
    public ClientDTO restoreClient(UUID id) {
        log.info("Restauration du client avec ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));

        if (client.getDeleted_at() == null) {
            throw new IllegalStateException("Ce client n'est pas supprimé");
        }

        client.setDeleted_at(null);
        client.setDeleted_by(null);
        Client restoredClient = clientRepository.save(client);

        log.info("Client restauré avec succès");
        return convertToDTO(restoredClient);
    }

    /**
     * Rechercher des clients par nom, prénom ou username
     */
    @Transactional(readOnly = true)
    public List<ClientDTO> searchClients(String keyword) {
        log.info("Recherche de clients avec le mot-clé: {}", keyword);
        return clientRepository.searchByNomOrPrenomOrUsername(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un client par username
     */
    @Transactional(readOnly = true)
    public ClientDTO getClientByUsername(String username) {
        log.info("Récupération du client avec username: {}", username);
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec le username: " + username));
        return convertToDTO(client);
    }

    /**
     * Convertir une entité Client en DTO
     */
    private ClientDTO convertToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setUsername(client.getUsername());
        dto.setTelephone(client.getTelephone());
        dto.setAdresse(client.getAdresse());
        dto.setDeletedBy(client.getDeleted_by());
        dto.setDeletedAt(client.getDeleted_at());
        return dto;
    }
}