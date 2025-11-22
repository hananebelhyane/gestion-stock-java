package com.gestiondestock.controller;

import com.gestiondestock.dto.AdminProfileDTO;
import com.gestiondestock.dto.ChangePasswordDTO;
import com.gestiondestock.service.AdminProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    /**
     * Récupérer le profil de l'admin connecté
     * GET /api/admins/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminProfileDTO>> getMyProfile(Authentication authentication) {
        log.info("Requête de récupération du profil pour: {}", authentication.getName());
        
        try {
            String username = authentication.getName();
            AdminProfileDTO profile = adminProfileService.getProfileByUsername(username);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil récupéré avec succès", profile));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du profil", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Mettre à jour le profil de l'admin connecté
     * PUT /api/admins/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminProfileDTO>> updateMyProfile(
            @Valid @RequestBody AdminProfileDTO profileDTO,
            Authentication authentication) {
        log.info("Requête de mise à jour du profil pour: {}", authentication.getName());
        
        try {
            String username = authentication.getName();
            AdminProfileDTO updatedProfile = adminProfileService.updateProfile(username, profileDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil mis à jour avec succès", updatedProfile));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du profil", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Changer le mot de passe de l'admin connecté
     * POST /api/admins/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            Authentication authentication) {
        log.info("Requête de changement de mot de passe pour: {}", authentication.getName());
        
        try {
            String username = authentication.getName();
            adminProfileService.changePassword(username, changePasswordDTO);
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