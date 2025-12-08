package com.gestiondestock.controller;

import com.gestiondestock.dto.ChangePasswordDTO;
import com.gestiondestock.dto.MagasinierProfileDTO;
import com.gestiondestock.service.MagasinierProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/magasiniers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MagasinierProfileController {

    private final MagasinierProfileService magasinierProfileService;

    /**
     * Récupérer le profil du magasinier connecté
     * GET /api/magasiniers/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('MAGASINIER')")
    public ResponseEntity<ApiResponse<MagasinierProfileDTO>> getMyProfile(Authentication authentication) {
        log.info("Requête de récupération du profil pour: {}", authentication.getName());
        
        try {
            String username = authentication.getName();
            MagasinierProfileDTO profile = magasinierProfileService.getProfileByUsername(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil récupéré avec succès", profile));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du profil", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Mettre à jour le profil du magasinier connecté
     * PUT /api/magasiniers/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('MAGASINIER')")
    public ResponseEntity<ApiResponse<MagasinierProfileDTO>> updateMyProfile(
            @Valid @RequestBody MagasinierProfileDTO profileDTO,
            Authentication authentication) {
        log.info("Requête de mise à jour du profil pour: {}", authentication.getName());
        
        try {
            String username = authentication.getName();
            MagasinierProfileDTO updatedProfile = magasinierProfileService.updateProfile(username, profileDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil mis à jour avec succès", updatedProfile));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du profil", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Changer le mot de passe du magasinier connecté
     * POST /api/magasiniers/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('MAGASINIER')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            Authentication authentication) {
        log.info("Requête de changement de mot de passe pour: {}", authentication.getName());
        
        try {
            String username = authentication.getName();
            magasinierProfileService.changePassword(username, changePasswordDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Mot de passe changé avec succès", null));
        } catch (Exception e) {
            log.error("Erreur lors du changement de mot de passe", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
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