package com.gestiondestock.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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


@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    /**
     * CrÃ©er un nouveau fournisseur
     * POST /api/fournisseurs
     * @param request
     * @return 
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FournisseurDTO>> createFournisseur(
            @Valid @RequestBody FournisseurRequestDTO request) {
        log.info("RequÃªte de crÃ©ation de fournisseur reÃ§ue");
        FournisseurDTO fournisseur = fournisseurService.createFournisseur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Fournisseur crÃ©Ã© avec succÃ¨s", fournisseur));
    }

    /**
     * RÃ©cupÃ©rer tous les fournisseurs actifs
     * GET /api/fournisseurs
     * @return 
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> getAllActiveFournisseurs() {
        log.info("RequÃªte de rÃ©cupÃ©ration de tous les fournisseurs actifs");
        List<FournisseurDTO> fournisseurs = fournisseurService.getAllActiveFournisseurs();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseurs rÃ©cupÃ©rÃ©s avec succÃ¨s", fournisseurs));
    }

    /**
     * RÃ©cupÃ©rer tous les fournisseurs supprimÃ©s
     * GET /api/fournisseurs/deleted
     * @return 
     */
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> getAllDeletedFournisseurs() {
        log.info("RequÃªte de rÃ©cupÃ©ration de tous les fournisseurs supprimÃ©s");
        List<FournisseurDTO> fournisseurs = fournisseurService.getAllDeletedFournisseurs();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseurs supprimÃ©s rÃ©cupÃ©rÃ©s avec succÃ¨s", fournisseurs));
    }

    /**
     * RÃ©cupÃ©rer un fournisseur par ID
     * GET /api/fournisseurs/{id}
     * @param id
     * @return 
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FournisseurDTO>> getFournisseurById(@PathVariable UUID id) {
        log.info("RequÃªte de rÃ©cupÃ©ration du fournisseur avec ID: {}", id);
        FournisseurDTO fournisseur = fournisseurService.getFournisseurById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur rÃ©cupÃ©rÃ© avec succÃ¨s", fournisseur));
    }

    /**
     * Mettre Ã  jour un fournisseur
     * PUT /api/fournisseurs/{id}
     * @param id
     * @param request
     * @return 
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FournisseurDTO>> updateFournisseur(
            @PathVariable UUID id,
            @Valid @RequestBody FournisseurRequestDTO request) {
        log.info("RequÃªte de mise Ã  jour du fournisseur avec ID: {}", id);
        FournisseurDTO fournisseur = fournisseurService.updateFournisseur(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur mis Ã  jour avec succÃ¨s", fournisseur));
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
        log.info("RequÃªte de suppression du fournisseur avec ID: {}", id);
        
        // Si deletedBy n'est pas fourni, utiliser un UUID par dÃ©faut
        UUID userId = deleted_by != null ? deleted_by : UUID.randomUUID();
        
        fournisseurService.deleteFournisseur(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur supprimÃ© avec succÃ¨s", null));
    }

    /**
     * Restaurer un fournisseur supprimÃ©
     * PATCH /api/fournisseurs/{id}/restore
     * @param id
     * @return 
     */
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<FournisseurDTO>> restoreFournisseur(@PathVariable UUID id) {
        log.info("RequÃªte de restauration du fournisseur avec ID: {}", id);
        FournisseurDTO fournisseur = fournisseurService.restoreFournisseur(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur restaurÃ© avec succÃ¨s", fournisseur));
    }

    /**
     * Rechercher des fournisseurs par nom ou prÃ©nom
     * GET /api/fournisseurs/search?keyword=...
     * @param keyword
     * @return 
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FournisseurDTO>>> searchFournisseurs(
            @RequestParam String keyword) {
        log.info("RequÃªte de recherche de fournisseurs avec le mot-clÃ©: {}", keyword);
        List<FournisseurDTO> fournisseurs = fournisseurService.searchFournisseurs(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recherche effectuÃ©e avec succÃ¨s", fournisseurs));
    }

    /**
     * RÃ©cupÃ©rer un fournisseur par email
     * GET /api/fournisseurs/by-email?email=...
     * @param email
     * @return 
     */
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<FournisseurDTO>> getFournisseurByEmail(
            @RequestParam String email) {
        log.info("RequÃªte de rÃ©cupÃ©ration du fournisseur avec email: {}", email);
        FournisseurDTO fournisseur = fournisseurService.getFournisseurByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fournisseur rÃ©cupÃ©rÃ© avec succÃ¨s", fournisseur));
    }

    /**
     * Classe interne pour structurer les rÃ©ponses API
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