package com.gestiondestock.service;

import com.gestiondestock.dto.ChangePasswordDTO;
import com.gestiondestock.dto.MagasinierProfileDTO;
import com.gestiondestock.entity.Magasinier;
import com.gestiondestock.repository.MagasinierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MagasinierProfileService {

    private final MagasinierRepository magasinierRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupérer le profil d'un magasinier par son username
     */
    @Transactional(readOnly = true)
    public MagasinierProfileDTO getProfileByUsername(String username) {
        log.info("Récupération du profil pour le magasinier: {}", username);
        
        Magasinier magasinier = magasinierRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Magasinier non trouvé: " + username));
        
        return convertToDTO(magasinier);
    }

    /**
     * Mettre à jour le profil d'un magasinier
     */
    public MagasinierProfileDTO updateProfile(String username, MagasinierProfileDTO profileDTO) {
        log.info("Mise à jour du profil pour le magasinier: {}", username);
        
        Magasinier magasinier = magasinierRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Magasinier non trouvé: " + username));
        
        // Vérifier si le téléphone est déjà utilisé par un autre magasinier
        if (!magasinier.getTelephone().equals(profileDTO.getTelephone())) {
            if (magasinierRepository.existsByTelephoneAndNotDeleted(profileDTO.getTelephone())) {
                throw new RuntimeException("Ce numéro de téléphone est déjà utilisé");
            }
        }
        
        // Mettre à jour les informations
        magasinier.setNom(profileDTO.getNom());
        magasinier.setPrenom(profileDTO.getPrenom());
        magasinier.setTelephone(profileDTO.getTelephone());
        
        Magasinier updatedMagasinier = magasinierRepository.save(magasinier);
        log.info("Profil mis à jour avec succès pour: {}", username);
        
        return convertToDTO(updatedMagasinier);
    }

    /**
     * Changer le mot de passe d'un magasinier
     */
    public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        log.info("Changement de mot de passe pour le magasinier: {}", username);
        
        Magasinier magasinier = magasinierRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Magasinier non trouvé: " + username));
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), magasinier.getMotDePasse())) {
            log.warn("Ancien mot de passe incorrect pour: {}", username);
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }
        
        // Vérifier que le nouveau mot de passe est différent de l'ancien
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), magasinier.getMotDePasse())) {
            throw new RuntimeException("Le nouveau mot de passe doit être différent de l'ancien");
        }
        
        // Encoder et sauvegarder le nouveau mot de passe
        magasinier.setMotDePasse(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        magasinierRepository.save(magasinier);
        
        log.info("Mot de passe changé avec succès pour: {}", username);
    }

    /**
     * Convertir une entité Magasinier en DTO
     */
    private MagasinierProfileDTO convertToDTO(Magasinier magasinier) {
        MagasinierProfileDTO dto = new MagasinierProfileDTO();
        dto.setId(magasinier.getId());
        dto.setNom(magasinier.getNom());
        dto.setPrenom(magasinier.getPrenom());
        dto.setUsername(magasinier.getUsername());
        dto.setTelephone(magasinier.getTelephone());
        return dto;
    }
}