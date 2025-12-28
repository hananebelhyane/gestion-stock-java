package com.gestiondestock.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestiondestock.dto.CommandeClientDTO;
import com.gestiondestock.dto.CommandeClientResponse;
import com.gestiondestock.dto.FactureDTO;
import com.gestiondestock.dto.LigneCommandeDTO;
import com.gestiondestock.dto.PanierItemRequest;
import com.gestiondestock.dto.PasserCommandeRequest;
import com.gestiondestock.entity.Client;
import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.service.CommandeClientService;
import com.gestiondestock.service.FactureService;

@RestController
@RequestMapping("/api/commandes")
public class CommandeClientController {

    private final CommandeClientService commandeService;
    private final FactureService factureService;

    public CommandeClientController(CommandeClientService commandeService, FactureService factureService) {
        this.commandeService = commandeService;
        this.factureService = factureService;
    }

    // ========== ADMIN/MAGASINIER ENDPOINTS ==========
    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    public List<CommandeClientResponse> getAllCommandesClient() {
        return commandeService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    public ResponseEntity<?> createCommandeClient(@RequestBody PasserCommandeRequest request) {
        try {
            commandeService.passerCommande(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Commande créée avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/clients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    public ResponseEntity<Void> deleteCommandeClient(@PathVariable String id) {
        commandeService.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    // ========== CLIENT ENDPOINTS (CHECKOUT FLOW) ==========

    /**
     * POST /api/commandes/panier/items - Persist cart change immediately.
     * Body: { produitId: UUID, quantite: int } where quantite is a delta (+1,
     * -1...).
     */
    @PostMapping("/panier/items")
    public ResponseEntity<CommandeClientDTO> addOrUpdatePanierItem(
            @RequestBody PanierItemRequest request,
            @RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID clientId = UUID.fromString(clientIdHeader);
            if (request == null || request.getProduitId() == null) {
                return ResponseEntity.badRequest().build();
            }
            CommandeClientDTO updated = commandeService.addOrUpdatePanierItem(clientId, request.getProduitId(),
                    request.getQuantite());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * DELETE /api/commandes/panier/items/{produitId} - Remove product line from
     * pending cart.
     */
    @DeleteMapping("/panier/items/{produitId}")
    public ResponseEntity<CommandeClientDTO> removePanierItem(
            @PathVariable String produitId,
            @RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID clientId = UUID.fromString(clientIdHeader);
            UUID produitUUID = UUID.fromString(produitId);
            CommandeClientDTO updated = commandeService.removePanierItem(clientId, produitUUID);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * POST /api/commandes/checkout - Client adds items to cart and checkout
     * Expects: List of LigneCommandeDTO with produitId, quantite, prixUnitaire
     */
    @PostMapping("/checkout")
    public ResponseEntity<CommandeClientDTO> checkout(
            @RequestBody List<LigneCommandeDTO> lignes,
            @RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID clientId = UUID.fromString(clientIdHeader);

            // Create or get pending commande
            CommandeClientDTO commande = commandeService.createOrGetPendingCommandeForClient(clientId);

            // Add all lignes to commande
            for (LigneCommandeDTO ligne : lignes) {
                commande = commandeService.addLigneCommande(commande.getId(), ligne);
            }

            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/commandes/pending - Get current pending commande for client
     */
    @GetMapping("/pending")
    public ResponseEntity<CommandeClientDTO> getPendingCommande(@RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID clientId = UUID.fromString(clientIdHeader);
            var commande = commandeService.getPendingCommandeForClient(clientId);

            if (commande.isPresent()) {
                return ResponseEntity.ok(commande.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/commandes/panier - Alias for /pending
     */
    @GetMapping("/panier")
    public ResponseEntity<CommandeClientDTO> getPanier(@RequestHeader("X-Client-Id") String clientIdHeader) {
        return getPendingCommande(clientIdHeader);
    }

    /**
     * POST /api/commandes/confirm/{commandeId} - Confirm order and generate invoice
     */
    @PostMapping("/{commandeId}/confirm")
    public ResponseEntity<?> confirmCommande(
            @PathVariable String commandeId,
            @RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID commandeUUID = UUID.fromString(commandeId);
            UUID clientId = UUID.fromString(clientIdHeader);

            // Verify commande exists
            var commande = commandeService.getCommandeById(commandeUUID);
            if (commande.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Confirm commande
            CommandeClientDTO confirmed = commandeService.confirmCommande(commandeUUID);

            // Generate facture
            FactureDTO facture = factureService.generateFacture(commandeUUID);

            // Return both commande and facture
            return ResponseEntity.ok(new Object() {
                public CommandeClientDTO commande_data = confirmed;
                public FactureDTO facture_data = facture;
            });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/commandes/{commandeId}/facture - Get invoice for order
     */
    @GetMapping("/{commandeId}/facture")
    public ResponseEntity<FactureDTO> getFacture(
            @PathVariable String commandeId,
            @RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID commandeUUID = UUID.fromString(commandeId);
            var facture = factureService.getFactureByCommandeId(commandeUUID);

            if (facture.isPresent()) {
                return ResponseEntity.ok(facture.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/commandes/client/history - Get all orders for client
     */
    @GetMapping("/client/history")
    public ResponseEntity<List<CommandeClientDTO>> getClientOrderHistory(
            @RequestHeader("X-Client-Id") String clientIdHeader) {
        try {
            UUID clientId = UUID.fromString(clientIdHeader);
            List<CommandeClientDTO> commandes = commandeService.getCommandesByClient(clientId);
            return ResponseEntity.ok(commandes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/passer")
    public ResponseEntity<?> passerCommande(@RequestBody PasserCommandeRequest request) {
        try {
            commandeService.passerCommande(request);
            return ResponseEntity.ok("Commande créée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/commandes/{commandeId} - Get commande details
     */
    @GetMapping("/{commandeId}")
    public ResponseEntity<CommandeClientDTO> getCommandeDetails(@PathVariable String commandeId) {
        try {
            UUID commandeUUID = UUID.fromString(commandeId);
            var commande = commandeService.getCommandeById(commandeUUID);

            if (commande.isPresent()) {
                return ResponseEntity.ok(commande.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private CommandeClientResponse toResponse(CommandeClient commande) {
        Client c = commande.getClient();
        CommandeClientResponse.ClientInfo clientInfo = null;
        if (c != null) {
            clientInfo = new CommandeClientResponse.ClientInfo(
                    c.getId(),
                    c.getNom(),
                    c.getPrenom(),
                    c.getUsername(),
                    c.getTelephone(),
                    c.getAdresse());
        }
        return new CommandeClientResponse(
                commande.getId(),
                clientInfo,
                commande.getDateCommande(),
                commande.getStatut() != null ? commande.getStatut().name() : "en_attente",
                commande.getSeuilMax());
    }
}
