package com.gestiondestock.controller;

import com.gestiondestock.dto.ChangePasswordDTO;
import com.gestiondestock.dto.ClientProfileDTO;
import com.gestiondestock.service.ClientProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClientProfileController {

    private final ClientProfileService clientProfileService;

    /**
     * GET /api/clients/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<ClientProfileDTO>> getMyProfile(Authentication authentication) {
        log.info("Récupération profil client: {}", authentication.getName());

        try {
            ClientProfileDTO profile = clientProfileService.getProfileByUsername(authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil récupéré avec succès", profile));
        } catch (Exception e) {
            log.error("Erreur récupération profil client", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * PUT /api/clients/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<ClientProfileDTO>> updateMyProfile(
            @Valid @RequestBody ClientProfileDTO profileDTO,
            Authentication authentication) {

        log.info("Mise à jour profil client: {}", authentication.getName());

        try {
            ClientProfileDTO updated = clientProfileService.updateProfile(authentication.getName(), profileDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil mis à jour avec succès", updated));
        } catch (Exception e) {
            log.error("Erreur mise à jour profil client", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * POST /api/clients/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordDTO dto,
            Authentication authentication) {

        log.info("Changement mot de passe client: {}", authentication.getName());

        try {
            clientProfileService.changePassword(authentication.getName(), dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Mot de passe changé avec succès", null));
        } catch (Exception e) {
            log.error("Erreur changement mot de passe client", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Réponse API générique
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

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }
}
