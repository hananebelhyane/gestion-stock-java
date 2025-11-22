package com.gestiondestock.service;

import com.gestiondestock.dto.AdminProfileDTO;
import com.gestiondestock.dto.ChangePasswordDTO;
import com.gestiondestock.entity.Admin;
import com.gestiondestock.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminProfileService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupérer le profil d'un admin par son username
     */
    @Transactional(readOnly = true)
    public AdminProfileDTO getProfileByUsername(String username) {
        log.info("Récupération du profil pour l'utilisateur: {}", username);
        
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));
        
        return convertToDTO(admin);
    }

    /**
     * Mettre à jour le profil d'un admin
     */
    public AdminProfileDTO updateProfile(String username, AdminProfileDTO profileDTO) {
        log.info("Mise à jour du profil pour l'utilisateur: {}", username);
        
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));
        
        // Vérifier si l'email est déjà utilisé par un autre admin
        if (!admin.getEmail().equals(profileDTO.getEmail())) {
            adminRepository.findByEmail(profileDTO.getEmail())
                    .ifPresent(existingAdmin -> {
                        if (!existingAdmin.getId().equals(admin.getId())) {
                            throw new RuntimeException("Cet email est déjà utilisé");
                        }
                    });
        }
        
        // Mettre à jour les informations
        admin.setNom(profileDTO.getNom());
        admin.setPrenom(profileDTO.getPrenom());
        admin.setEmail(profileDTO.getEmail());
        admin.setTelephone(profileDTO.getTelephone());
        
        Admin updatedAdmin = adminRepository.save(admin);
        log.info("Profil mis à jour avec succès pour: {}", username);
        
        return convertToDTO(updatedAdmin);
    }

    /**
     * Changer le mot de passe d'un admin
     */
    public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        log.info("Changement de mot de passe pour l'utilisateur: {}", username);
        
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + username));
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), admin.getMotDePasse())) {
            log.warn("Ancien mot de passe incorrect pour: {}", username);
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }
        
        // Vérifier que le nouveau mot de passe est différent de l'ancien
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), admin.getMotDePasse())) {
            throw new RuntimeException("Le nouveau mot de passe doit être différent de l'ancien");
        }
        
        // Encoder et sauvegarder le nouveau mot de passe
        admin.setMotDePasse(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        adminRepository.save(admin);
        
        log.info("Mot de passe changé avec succès pour: {}", username);
    }

    /**
     * Convertir une entité Admin en DTO
     */
    private AdminProfileDTO convertToDTO(Admin admin) {
        AdminProfileDTO dto = new AdminProfileDTO();
        dto.setId(admin.getId());
        dto.setNom(admin.getNom());
        dto.setPrenom(admin.getPrenom());
        dto.setEmail(admin.getEmail());
        dto.setUsername(admin.getUsername());
        dto.setTelephone(admin.getTelephone());
        return dto;
    }
}