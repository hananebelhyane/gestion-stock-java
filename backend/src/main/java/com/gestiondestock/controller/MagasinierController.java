package com.gestiondestock.controller;

import com.gestiondestock.dto.MagasinierDTO;
import com.gestiondestock.dto.MagasinierRequestDTO;
import com.gestiondestock.service.MagasinierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/magasiniers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class MagasinierController {

    private final MagasinierService magasinierService;

    /**
     * Créer un nouveau magasinier
     * POST /api/magasiniers
     * @param request
     * @return 
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MagasinierDTO>> createMagasinier(
            @Valid @RequestBody MagasinierRequestDTO request) {
        log.info("Requête de création de magasinier reçue");
        MagasinierDTO magasinier = magasinierService.createMagasinier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Magasinier créé avec succès", magasinier));
    }

    /**
     * Récupérer tous les magasiniers actifs
     * GET /api/magasiniers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MagasinierDTO>>> getAllActiveMagasiniers() {
        log.info("Requête de récupération de tous les magasiniers actifs");
        List<MagasinierDTO> magasiniers = magasinierService.getAllActiveMagasiniers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasiniers récupérés avec succès", magasiniers));
    }

    /**
     * Récupérer tous les magasiniers supprimés
     * GET /api/magasiniers/deleted
     */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<MagasinierDTO>>> getAllDeletedMagasiniers() {
        log.info("Requête de récupération de tous les magasiniers supprimés");
        List<MagasinierDTO> magasiniers = magasinierService.getAllDeletedMagasiniers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasiniers supprimés récupérés avec succès", magasiniers));
    }

    /**
     * Récupérer un magasinier par ID
     * GET /api/magasiniers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MagasinierDTO>> getMagasinierById(@PathVariable UUID id) {
        log.info("Requête de récupération du magasinier avec ID: {}", id);
        MagasinierDTO magasinier = magasinierService.getMagasinierById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasinier récupéré avec succès", magasinier));
    }

    /**
     * Mettre à jour un magasinier
     * PUT /api/magasiniers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MagasinierDTO>> updateMagasinier(
            @PathVariable UUID id,
            @Valid @RequestBody MagasinierRequestDTO request) {
        log.info("Requête de mise à jour du magasinier avec ID: {}", id);
        MagasinierDTO magasinier = magasinierService.updateMagasinier(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasinier mis à jour avec succès", magasinier));
    }

    /**
     * Supprimer un magasinier (soft delete)
     * DELETE /api/magasiniers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMagasinier(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID deleted_by) {
        log.info("Requête de suppression du magasinier avec ID: {}", id);
        
        // Si deleted_by n'est pas fourni, utiliser un UUID par défaut
        UUID userId = deleted_by != null ? deleted_by : UUID.randomUUID();
        
        magasinierService.deleteMagasinier(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasinier supprimé avec succès", null));
    }

    /**
     * Restaurer un magasinier supprimé
     * PATCH /api/magasiniers/{id}/restore
     */
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MagasinierDTO>> restoreMagasinier(@PathVariable UUID id) {
        log.info("Requête de restauration du magasinier avec ID: {}", id);
        MagasinierDTO magasinier = magasinierService.restoreMagasinier(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasinier restauré avec succès", magasinier));
    }

    /**
     * Rechercher des magasiniers par nom, prénom ou username
     * GET /api/magasiniers/search?keyword=...
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MagasinierDTO>>> searchMagasiniers(
            @RequestParam String keyword) {
        log.info("Requête de recherche de magasiniers avec le mot-clé: {}", keyword);
        List<MagasinierDTO> magasiniers = magasinierService.searchMagasiniers(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recherche effectuée avec succès", magasiniers));
    }

    /**
     * Récupérer un magasinier par username
     * GET /api/magasiniers/by-username?username=...
     */
    @GetMapping("/by-username")
    public ResponseEntity<ApiResponse<MagasinierDTO>> getMagasinierByUsername(
            @RequestParam String username) {
        log.info("Requête de récupération du magasinier avec username: {}", username);
        MagasinierDTO magasinier = magasinierService.getMagasinierByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Magasinier récupéré avec succès", magasinier));
    }

    /**
     * Classe interne pour structurer les réponses API
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