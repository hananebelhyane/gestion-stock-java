package com.gestiondestock.service;

import com.gestiondestock.dto.MagasinierDTO;
import com.gestiondestock.dto.MagasinierRequestDTO;
import com.gestiondestock.entity.Magasinier;
import com.gestiondestock.exception.ResourceNotFoundException;
import com.gestiondestock.exception.DuplicateResourceException;
import com.gestiondestock.repository.MagasinierRepository;

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
public class MagasinierService {

    private final MagasinierRepository magasinierRepository;

    /**
     * Créer un nouveau magasinier
     */
    public MagasinierDTO createMagasinier(MagasinierRequestDTO request) {
        log.info("Création d'un nouveau magasinier avec username: {}", request.getUsername());

        if (magasinierRepository.existsByUsernameAndNotDeleted(request.getUsername())) {
            throw new DuplicateResourceException("Un magasinier avec ce username existe déjà");
        }

        if (magasinierRepository.existsByTelephoneAndNotDeleted(request.getTelephone())) {
            throw new DuplicateResourceException("Un magasinier avec ce téléphone existe déjà");
        }

        Magasinier magasinier = new Magasinier();
        magasinier.setNom(request.getNom());
        magasinier.setPrenom(request.getPrenom());
        magasinier.setUsername(request.getUsername());
        magasinier.setTelephone(request.getTelephone());
        magasinier.setMotDePasse(request.getMotDePasse());

        Magasinier savedMagasinier = magasinierRepository.save(magasinier);
        log.info("Magasinier créé avec succès avec ID: {}", savedMagasinier.getId());

        return convertToDTO(savedMagasinier);
    }

    /**
     * Récupérer tous les magasiniers actifs
     */
    @Transactional(readOnly = true)
    public List<MagasinierDTO> getAllActiveMagasiniers() {
        log.info("Récupération de tous les magasiniers actifs");
        return magasinierRepository.findAllActive()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les magasiniers supprimés
     */
    @Transactional(readOnly = true)
    public List<MagasinierDTO> getAllDeletedMagasiniers() {
        log.info("Récupération de tous les magasiniers supprimés");
        return magasinierRepository.findAllDeleted()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un magasinier par ID
     */
    @Transactional(readOnly = true)
    public MagasinierDTO getMagasinierById(UUID id) {
        log.info("Récupération du magasinier avec ID: {}", id);
        Magasinier magasinier = magasinierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Magasinier non trouvé avec l'ID: " + id));
        return convertToDTO(magasinier);
    }

    /**
     * Mettre à jour un magasinier
     */
    public MagasinierDTO updateMagasinier(UUID id, MagasinierRequestDTO request) {
        log.info("Mise à jour du magasinier avec ID: {}", id);

        Magasinier magasinier = magasinierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Magasinier non trouvé avec l'ID: " + id));

        if (magasinier.getDeleted_at() != null) {
            throw new IllegalStateException("Impossible de modifier un magasinier supprimé");
        }

        if (!magasinier.getUsername().equals(request.getUsername()) &&
                magasinierRepository.existsByUsernameAndNotDeleted(request.getUsername())) {
            throw new DuplicateResourceException("Un magasinier avec ce username existe déjà");
        }

        if (!magasinier.getTelephone().equals(request.getTelephone()) &&
                magasinierRepository.existsByTelephoneAndNotDeleted(request.getTelephone())) {
            throw new DuplicateResourceException("Un magasinier avec ce téléphone existe déjà");
        }

        magasinier.setNom(request.getNom());
        magasinier.setPrenom(request.getPrenom());
        magasinier.setUsername(request.getUsername());
        magasinier.setTelephone(request.getTelephone());
        magasinier.setMotDePasse(request.getMotDePasse());

        Magasinier updatedMagasinier = magasinierRepository.save(magasinier);
        log.info("Magasinier mis à jour avec succès");

        return convertToDTO(updatedMagasinier);
    }

    /**
     * Supprimer un magasinier (soft delete)
     */
    public void deleteMagasinier(UUID id, UUID deletedBy) {
        log.info("Suppression du magasinier avec ID: {} par l'utilisateur: {}", id, deletedBy);

        Magasinier magasinier = magasinierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Magasinier non trouvé avec l'ID: " + id));

        if (magasinier.getDeleted_at() != null) {
            throw new IllegalStateException("Ce magasinier est déjà supprimé");
        }

        magasinier.setDeleted_at(LocalDateTime.now());
        magasinier.setDeleted_by(deletedBy);
        magasinierRepository.save(magasinier);

        log.info("Magasinier supprimé avec succès");
    }

    /**
     * Restaurer un magasinier supprimé
     */
    public MagasinierDTO restoreMagasinier(UUID id) {
        log.info("Restauration du magasinier avec ID: {}", id);

        Magasinier magasinier = magasinierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Magasinier non trouvé avec l'ID: " + id));

        if (magasinier.getDeleted_at() == null) {
            throw new IllegalStateException("Ce magasinier n'est pas supprimé");
        }

        magasinier.setDeleted_at(null);
        magasinier.setDeleted_by(null);
        Magasinier restoredMagasinier = magasinierRepository.save(magasinier);

        log.info("Magasinier restauré avec succès");
        return convertToDTO(restoredMagasinier);
    }

    /**
     * Rechercher des magasiniers par nom, prénom ou username
     */
    @Transactional(readOnly = true)
    public List<MagasinierDTO> searchMagasiniers(String keyword) {
        log.info("Recherche de magasiniers avec le mot-clé: {}", keyword);
        return magasinierRepository.searchByNomOrPrenomOrUsername(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un magasinier par username
     */
    @Transactional(readOnly = true)
    public MagasinierDTO getMagasinierByUsername(String username) {
        log.info("Récupération du magasinier avec username: {}", username);
        Magasinier magasinier = magasinierRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Magasinier non trouvé avec le username: " + username));
        return convertToDTO(magasinier);
    }

    /**
     * Convertir une entité Magasinier en DTO
     */
    private MagasinierDTO convertToDTO(Magasinier magasinier) {
        MagasinierDTO dto = new MagasinierDTO();
        dto.setId(magasinier.getId());
        dto.setNom(magasinier.getNom());
        dto.setPrenom(magasinier.getPrenom());
        dto.setUsername(magasinier.getUsername());
        dto.setTelephone(magasinier.getTelephone());
        dto.setDeletedBy(magasinier.getDeleted_by());
        dto.setDeletedAt(magasinier.getDeleted_at());
        return dto;
    }
}