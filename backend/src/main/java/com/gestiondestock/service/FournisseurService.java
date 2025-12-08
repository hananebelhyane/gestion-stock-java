package com.gestiondestock.service;

import com.gestiondestock.dto.FournisseurDTO;
import com.gestiondestock.dto.FournisseurRequestDTO;
import com.gestiondestock.entity.Fournisseur;
import com.gestiondestock.exception.ResourceNotFoundException;
import com.gestiondestock.exception.DuplicateResourceException;
import com.gestiondestock.repository.FournisseurRepository;

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
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    /**
     * Créer un nouveau fournisseur
     * @param request
     * @return 
     */
    public FournisseurDTO createFournisseur(FournisseurRequestDTO request) {
        log.info("Création d'un nouveau fournisseur avec email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (fournisseurRepository.existsByEmailAndNotDeleted(request.getEmail())) {
            throw new DuplicateResourceException("Un fournisseur avec cet email existe déjà");
        }

        // Vérifier si le téléphone existe déjà
        if (fournisseurRepository.existsByTelephoneAndNotDeleted(request.getTelephone())) {
            throw new DuplicateResourceException("Un fournisseur avec ce téléphone existe déjà");
        }

        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom(request.getNom());
        fournisseur.setPrenom(request.getPrenom());
        fournisseur.setEmail(request.getEmail());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setAdresse(request.getAdresse());

        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        log.info("Fournisseur créé avec succès avec ID: {}", savedFournisseur.getId());

        return convertToDTO(savedFournisseur);
    }

    /**
     * Récupérer tous les fournisseurs actifs
     * @return 
     */
    @Transactional(readOnly = true)
    public List<FournisseurDTO> getAllActiveFournisseurs() {
        log.info("Récupération de tous les fournisseurs actifs");
        return fournisseurRepository.findAllActive()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les fournisseurs supprimés
     * @return 
     */
    @Transactional(readOnly = true)
    public List<FournisseurDTO> getAllDeletedFournisseurs() {
        log.info("Récupération de tous les fournisseurs supprimés");
        return fournisseurRepository.findAllDeleted()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un fournisseur par ID
     * @param id
     * @return 
     */
    @Transactional(readOnly = true)
    public FournisseurDTO getFournisseurById(UUID id) {
        log.info("Récupération du fournisseur avec ID: {}", id);
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'ID: " + id));
        
        return convertToDTO(fournisseur);
    }

    /**
     * Mettre à jour un fournisseur
     * @param id
     * @param request
     * @return 
     */
    public FournisseurDTO updateFournisseur(UUID id, FournisseurRequestDTO request) {
        log.info("Mise à jour du fournisseur avec ID: {}", id);

        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'ID: " + id));

        // Vérifier si le fournisseur est supprimé
        if (fournisseur.getDeleted_at() != null) {
            throw new IllegalStateException("Impossible de modifier un fournisseur supprimé");
        }

        // Vérifier l'unicité de l'email (si changé)
        if (!fournisseur.getEmail().equals(request.getEmail()) && 
            fournisseurRepository.existsByEmailAndNotDeleted(request.getEmail())) {
            throw new DuplicateResourceException("Un fournisseur avec cet email existe déjà");
        }

        // Vérifier l'unicité du téléphone (si changé)
        if (!fournisseur.getTelephone().equals(request.getTelephone()) && 
            fournisseurRepository.existsByTelephoneAndNotDeleted(request.getTelephone())) {
            throw new DuplicateResourceException("Un fournisseur avec ce téléphone existe déjà");
        }

        fournisseur.setNom(request.getNom());
        fournisseur.setPrenom(request.getPrenom());
        fournisseur.setEmail(request.getEmail());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setAdresse(request.getAdresse());

        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);
        log.info("Fournisseur mis à jour avec succès");

        return convertToDTO(updatedFournisseur);
    }

    /**
     * Supprimer un fournisseur (soft delete)
     * @param id
     * @param deleted_by
     */
    public void deleteFournisseur(UUID id, UUID deleted_by) {
        log.info("Suppression du fournisseur avec ID: {} par l'utilisateur: {}", id, deleted_by);

        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'ID: " + id));

        if (fournisseur.getDeleted_at() != null) {
            throw new IllegalStateException("Ce fournisseur est déjà supprimé");
        }

        fournisseur.setDeleted_at(LocalDateTime.now());
        fournisseur.setDeleted_by(deleted_by);
        fournisseurRepository.save(fournisseur);

        log.info("Fournisseur supprimé avec succès");
    }

    /**
     * Restaurer un fournisseur supprimé
     * @param id
     * @return 
     */
    public FournisseurDTO restoreFournisseur(UUID id) {
        log.info("Restauration du fournisseur avec ID: {}", id);

        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'ID: " + id));

        if (fournisseur.getDeleted_at() == null) {
            throw new IllegalStateException("Ce fournisseur n'est pas supprimé");
        }

        fournisseur.setDeleted_at(null);
        fournisseur.setDeleted_by(null);
        Fournisseur restoredFournisseur = fournisseurRepository.save(fournisseur);

        log.info("Fournisseur restauré avec succès");
        return convertToDTO(restoredFournisseur);
    }

    /**
     * Rechercher des fournisseurs par nom ou prénom
     * @param keyword
     * @return 
     */
    @Transactional(readOnly = true)
    public List<FournisseurDTO> searchFournisseurs(String keyword) {
        log.info("Recherche de fournisseurs avec le mot-clé: {}", keyword);
        return fournisseurRepository.searchByNomOrPrenom(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un fournisseur par email
     * @param email
     * @return 
     */
    @Transactional(readOnly = true)
    public FournisseurDTO getFournisseurByEmail(String email) {
        log.info("Récupération du fournisseur avec email: {}", email);
        Fournisseur fournisseur = fournisseurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'email: " + email));
        
        return convertToDTO(fournisseur);
    }

    /**
     * Convertir une entité Fournisseur en DTO
     */
    private FournisseurDTO convertToDTO(Fournisseur fournisseur) {
        FournisseurDTO dto = new FournisseurDTO();
        dto.setId(fournisseur.getId());
        dto.setNom(fournisseur.getNom());
        dto.setPrenom(fournisseur.getPrenom());
        dto.setEmail(fournisseur.getEmail());
        dto.setTelephone(fournisseur.getTelephone());
        dto.setAdresse(fournisseur.getAdresse());
        dto.setDeleted_by(fournisseur.getDeleted_by());
        dto.setDeleted_at(fournisseur.getDeleted_at());
        return dto;
    }
}
