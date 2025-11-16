package com.gestiondestock.controller;

import com.gestiondestock.dto.FournisseurDTO;
import com.gestiondestock.dto.FournisseurRequestDTO;
import com.gestiondestock.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: Ajouter @PreAuthorize("hasRole('ADMIN')") avant livraison finale
// Actuellement ouvert pour faciliter les tests en équipe
@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    /**
     * Créer un nouveau fournisseur
     * POST /api/fournisseurs
     * @param request
     * @return 
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FournisseurDTO>> createFournisseur(
            @Valid @RequestBody FournisseurRequestDTO request) {
        log.info("Requête de création de fournisseur reçue");
        FournisseurDTO fournisseur = fournisseurService.createFournisseur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Fournisseur créé avec succès", fournisseur));
    }

    /**
     * Récupérer tous les fournisseurs actifs
     * GET /api/fournisseurs
     * @return 
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> getAllActiveFournisseurs() {
        log.info("Requête de récupération de tous les fournisseurs actifs");
        List<FournisseurDTO> fournisseurs = fournisseurService.getAllActiveFournisseurs();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseurs récupérés avec succès", fournisseurs));
    }

    /**
     * Récupérer tous les fournisseurs supprimés
     * GET /api/fournisseurs/deleted
     * @return 
     */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> getAllDeletedFournisseurs() {
        log.info("Requête de récupération de tous les fournisseurs supprimés");
        List<FournisseurDTO> fournisseurs = fournisseurService.getAllDeletedFournisseurs();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseurs supprimés récupérés avec succès", fournisseurs));
    }

    /**
     * Récupérer un fournisseur par ID
     * GET /api/fournisseurs/{id}
     * @param id
     * @return 
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FournisseurDTO>> getFournisseurById(@PathVariable UUID id) {
        log.info("Requête de récupération du fournisseur avec ID: {}", id);
        FournisseurDTO fournisseur = fournisseurService.getFournisseurById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur récupéré avec succès", fournisseur));
    }

    /**
     * Mettre à jour un fournisseur
     * PUT /api/fournisseurs/{id}
     * @param id
     * @param request
     * @return 
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FournisseurDTO>> updateFournisseur(
            @PathVariable UUID id,
            @Valid @RequestBody FournisseurRequestDTO request) {
        log.info("Requête de mise à jour du fournisseur avec ID: {}", id);
        FournisseurDTO fournisseur = fournisseurService.updateFournisseur(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur mis à jour avec succès", fournisseur));
    }

    /**
     * Supprimer un fournisseur (soft delete)
     * DELETE /api/fournisseurs/{id}
     * @param id
     * @param deleted_by
     * @return 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFournisseur(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID deleted_by) {
        log.info("Requête de suppression du fournisseur avec ID: {}", id);
        
        // Si deletedBy n'est pas fourni, utiliser un UUID par défaut
        UUID userId = deleted_by != null ? deleted_by : UUID.randomUUID();
        
        fournisseurService.deleteFournisseur(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur supprimé avec succès", null));
    }

    /**
     * Restaurer un fournisseur supprimé
     * PATCH /api/fournisseurs/{id}/restore
     * @param id
     * @return 
     */
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<FournisseurDTO>> restoreFournisseur(@PathVariable UUID id) {
        log.info("Requête de restauration du fournisseur avec ID: {}", id);
        FournisseurDTO fournisseur = fournisseurService.restoreFournisseur(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur restauré avec succès", fournisseur));
    }

    /**
     * Rechercher des fournisseurs par nom ou prénom
     * GET /api/fournisseurs/search?keyword=...
     * @param keyword
     * @return 
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> searchFournisseurs(
            @RequestParam String keyword) {
        log.info("Requête de recherche de fournisseurs avec le mot-clé: {}", keyword);
        List<FournisseurDTO> fournisseurs = fournisseurService.searchFournisseurs(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recherche effectuée avec succès", fournisseurs));
    }

    /**
     * Récupérer un fournisseur par email
     * GET /api/fournisseurs/by-email?email=...
     * @param email
     * @return 
     */
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<FournisseurDTO>> getFournisseurByEmail(
            @RequestParam String email) {
        log.info("Requête de récupération du fournisseur avec email: {}", email);
        FournisseurDTO fournisseur = fournisseurService.getFournisseurByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur récupéré avec succès", fournisseur));
    }

    /**
     * Classe interne pour structurer les réponses API
     * @param <T>
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters et Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
