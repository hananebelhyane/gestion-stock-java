package com.gestiondestock.controller;

import com.gestiondestock.dto.ClientDTO;
import com.gestiondestock.dto.ClientRequestDTO;
import com.gestiondestock.entity.Client;
import com.gestiondestock.service.ClientService;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    // ================================
    // PUBLIC : inscription / register
    // ================================
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Client> registerClient(@RequestBody Client client) {
        log.info("Nouvelle inscription client avec username: {}", client.getUsername());
        Client savedClient = clientService.registerClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }
    // ================================
    // ADMIN : CRUD et gestion clients
    // ================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ClientDTO>> createClient(
            @Valid @RequestBody ClientRequestDTO request) {
        log.info("Requête de création de client reçue");
        ClientDTO client = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Client créé avec succès", client));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientDTO>>> getAllActiveClients() {
        log.info("Requête de récupération de tous les clients actifs");
        List<ClientDTO> clients = clientService.getAllActiveClients();
        return ResponseEntity.ok(new ApiResponse<>(true, "Clients récupérés avec succès", clients));
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<ClientDTO>>> getAllDeletedClients() {
        log.info("Requête de récupération de tous les clients supprimés");
        List<ClientDTO> clients = clientService.getAllDeletedClients();
        return ResponseEntity.ok(new ApiResponse<>(true, "Clients supprimés récupérés avec succès", clients));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientDTO>> getClientById(@PathVariable UUID id) {
        log.info("Requête de récupération du client avec ID: {}", id);
        ClientDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client récupéré avec succès", client));
    }

     @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientDTO>> updateClient(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequestDTO request) {
        log.info("Requête de mise à jour du client avec ID: {}", id);
        ClientDTO client = clientService.updateClient(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client mis à jour avec succès", client));
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID deleted_by) {
        log.info("Requête de suppression du client avec ID: {}", id);
        
        // Si deleted_by n'est pas fourni, utiliser un UUID par défaut
        UUID userId = deleted_by != null ? deleted_by : UUID.randomUUID();
        
        clientService.deleteClient(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client supprimé avec succès", null));
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<ClientDTO>> restoreClient(@PathVariable UUID id) {
        log.info("Requête de restauration du client avec ID: {}", id);
        ClientDTO client = clientService.restoreClient(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client restauré avec succès", client));
    }

    
     @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ClientDTO>>> searchClients(
            @RequestParam String keyword) {
        log.info("Requête de recherche de clients avec le mot-clé: {}", keyword);
        List<ClientDTO> clients = clientService.searchClients(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recherche effectuée avec succès", clients));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-username")
    public ResponseEntity<ApiResponse<ClientDTO>> getClientByUsername(
            @RequestParam String username) {
        log.info("Requête de récupération du client avec username: {}", username);
        ClientDTO client = clientService.getClientByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Client récupéré avec succès", client));
    }

    // ================================
    // Classe interne ApiResponse
    // ================================
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