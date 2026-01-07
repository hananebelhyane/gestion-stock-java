package com.gestiondestock.service;

import com.gestiondestock.dto.ChangePasswordDTO;
import com.gestiondestock.dto.ClientProfileDTO;
import com.gestiondestock.entity.Client;
import com.gestiondestock.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientProfileService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupérer le profil du client par username
     */
    @Transactional(readOnly = true)
    public ClientProfileDTO getProfileByUsername(String username) {
        log.info("Récupération du profil client: {}", username);

        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + username));

        return convertToDTO(client);
    }

    /**
     * Mettre à jour le profil du client
     */
    public ClientProfileDTO updateProfile(String username, ClientProfileDTO profileDTO) {
        log.info("Mise à jour du profil client: {}", username);

        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + username));

        // Vérifier téléphone unique
        if (!client.getTelephone().equals(profileDTO.getTelephone())) {
            if (clientRepository.existsByTelephoneAndNotDeleted(profileDTO.getTelephone())) {
                throw new RuntimeException("Ce numéro de téléphone est déjà utilisé");
            }
        }

        client.setNom(profileDTO.getNom());
        client.setPrenom(profileDTO.getPrenom());
        client.setTelephone(profileDTO.getTelephone());
        client.setAdresse(profileDTO.getAdresse());

        Client updatedClient = clientRepository.save(client);
        log.info("Profil client mis à jour avec succès: {}", username);

        return convertToDTO(updatedClient);
    }

    /**
     * Changer le mot de passe du client
     */
    public void changePassword(String username, ChangePasswordDTO dto) {
        log.info("Changement de mot de passe client: {}", username);

        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + username));

        if (!passwordEncoder.matches(dto.getOldPassword(), client.getMotDePasse())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), client.getMotDePasse())) {
            throw new RuntimeException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        client.setMotDePasse(passwordEncoder.encode(dto.getNewPassword()));
        clientRepository.save(client);

        log.info("Mot de passe client changé avec succès: {}", username);
    }

    /**
     * Mapper Entity → DTO
     */
    private ClientProfileDTO convertToDTO(Client client) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setUsername(client.getUsername());
        dto.setTelephone(client.getTelephone());
        dto.setAdresse(client.getAdresse());
        return dto;
    }
}
