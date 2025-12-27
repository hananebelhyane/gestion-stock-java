package com.gestiondestock.service;

import com.gestiondestock.dto.CommandeClientDTO;
import com.gestiondestock.dto.CommandeClientRequest;
import com.gestiondestock.dto.LigneCommandeDTO;
import com.gestiondestock.entity.*;
import com.gestiondestock.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommandeClientService {

    private final CommandeClientRepository commandeClientRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;

    public CommandeClientDTO addOrUpdatePanierItem(UUID clientId, UUID produitId, int quantiteDelta) {
        CommandeClientDTO pending = createOrGetPendingCommandeForClient(clientId);
        UUID commandeId = pending.getId();

        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found"));
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit not found"));

        LigneCommande ligne = ligneCommandeRepository.findByCommandeIdAndProduitId(commandeId, produitId)
                .orElseGet(() -> {
                    LigneCommande l = new LigneCommande();
                    l.setCommande(commande);
                    l.setProduit(produit);
                    l.setQuantite(0);
                    l.setPrixUnitaire(produit.getPrixUnitaire());
                    l.setMontantTotal(0.0);
                    return l;
                });

        int currentQty = ligne.getQuantite() != null ? ligne.getQuantite() : 0;
        int newQty = currentQty + quantiteDelta;

        if (newQty <= 0) {
            if (ligne.getId() != null) {
                ligneCommandeRepository.deleteById(ligne.getId());
            }
        } else {
            Double prix = produit.getPrixUnitaire() != null ? produit.getPrixUnitaire() : 0.0;
            ligne.setQuantite(newQty);
            ligne.setPrixUnitaire(prix);
            ligne.setMontantTotal(newQty * prix);
            ligneCommandeRepository.save(ligne);
        }

        CommandeClient updated = commandeClientRepository.findById(commandeId).get();
        return convertToDTO(updated);
    }

    public CommandeClientDTO removePanierItem(UUID clientId, UUID produitId) {
        Optional<CommandeClient> existing = commandeClientRepository
                .findFirstByClientIdAndStatutOrderByDateCommandeDesc(clientId,
                        CommandeClient.StatutCommande.en_attente);
        if (existing.isEmpty()) {
            throw new RuntimeException("No pending commande");
        }
        UUID commandeId = existing.get().getId();

        ligneCommandeRepository.findByCommandeIdAndProduitId(commandeId, produitId)
                .ifPresent(l -> ligneCommandeRepository.deleteById(l.getId()));

        CommandeClient updated = commandeClientRepository.findById(commandeId).get();
        return convertToDTO(updated);
    }

    public List<CommandeClient> findAll() {
        return commandeClientRepository.findAll();
    }

    public CommandeClient save(CommandeClient commande) {
        return commandeClientRepository.save(commande);
    }

    public void deleteById(UUID id) {
        commandeClientRepository.deleteById(id);
    }

    public CommandeClientDTO createCommande(CommandeClientDTO commandeClientDTO) {
        CommandeClient commande = convertToEntity(commandeClientDTO);
        CommandeClient saved = commandeClientRepository.save(commande);
        return convertToDTO(saved);
    }

    public Optional<CommandeClientDTO> getCommandeById(UUID id) {
        return commandeClientRepository.findById(id).map(this::convertToDTO);
    }

    public List<CommandeClientDTO> getAllCommandes() {
        return commandeClientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CommandeClientDTO> getCommandesByClient(UUID clientId) {
        return commandeClientRepository.findByClientId(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CommandeClientDTO> getPendingCommandeForClient(UUID clientId) {
        return commandeClientRepository
                .findFirstByClientIdAndStatutOrderByDateCommandeDesc(clientId, CommandeClient.StatutCommande.en_attente)
                .map(this::convertToDTO);
    }

    public CommandeClientDTO createOrGetPendingCommandeForClient(UUID clientId) {
        Optional<CommandeClient> existing = commandeClientRepository
                .findFirstByClientIdAndStatutOrderByDateCommandeDesc(clientId,
                        CommandeClient.StatutCommande.en_attente);

        if (existing.isPresent()) {
            return convertToDTO(existing.get());
        }

        // Create new pending commande
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        CommandeClient newCommande = new CommandeClient();
        newCommande.setClient(client);
        newCommande.setDateCommande(LocalDateTime.now());
        newCommande.setStatut(CommandeClient.StatutCommande.en_attente);

        CommandeClient saved = commandeClientRepository.save(newCommande);
        return convertToDTO(saved);
    }

    public CommandeClientDTO addLigneCommande(UUID commandeId, LigneCommandeDTO ligneDTO) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        Produit produit = produitRepository.findById(ligneDTO.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit not found"));

        LigneCommande ligne = ligneCommandeRepository.findByCommandeIdAndProduitId(commandeId, produit.getId())
                .orElseGet(() -> {
                    LigneCommande l = new LigneCommande();
                    l.setCommande(commande);
                    l.setProduit(produit);
                    l.setQuantite(0);
                    return l;
                });

        int qtyToAdd = ligneDTO.getQuantite() != null ? ligneDTO.getQuantite() : 0;
        int newQty = (ligne.getQuantite() != null ? ligne.getQuantite() : 0) + qtyToAdd;
        Double prix = produit.getPrixUnitaire() != null ? produit.getPrixUnitaire() : 0.0;
        ligne.setQuantite(Math.max(newQty, 0));
        ligne.setPrixUnitaire(prix);
        ligne.setMontantTotal(ligne.getQuantite() * prix);

        ligneCommandeRepository.save(ligne);

        CommandeClient updated = commandeClientRepository.findById(commandeId).get();
        return convertToDTO(updated);
    }

    public CommandeClientDTO removeLigneCommande(UUID commandeId, UUID ligneId) {
        ligneCommandeRepository.deleteById(ligneId);
        CommandeClient updated = commandeClientRepository.findById(commandeId).get();
        return convertToDTO(updated);
    }

    public CommandeClientDTO confirmCommande(UUID commandeId) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        commande.setStatut(CommandeClient.StatutCommande.confirmee);
        CommandeClient saved = commandeClientRepository.save(commande);
        return convertToDTO(saved);
    }

    public CommandeClientDTO cancelCommande(UUID commandeId) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        commande.setStatut(CommandeClient.StatutCommande.annulee);
        CommandeClient saved = commandeClientRepository.save(commande);
        return convertToDTO(saved);
    }

    public CommandeClientDTO updateCommande(UUID id, CommandeClientDTO commandeClientDTO) {
        CommandeClient existing = commandeClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        if (commandeClientDTO.getStatut() != null) {
            try {
                existing.setStatut(CommandeClient.StatutCommande.valueOf(commandeClientDTO.getStatut()));
            } catch (IllegalArgumentException e) {
                // Keep existing statut
            }
        }

        CommandeClient updated = commandeClientRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteCommande(UUID id) {
        commandeClientRepository.deleteById(id);
    }

    public List<CommandeClientDTO> getCommandesByStatut(String statut) {
        try {
            CommandeClient.StatutCommande enumStatut = CommandeClient.StatutCommande.valueOf(statut);
            return commandeClientRepository.findByStatut(enumStatut)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    // Helper methods
    private CommandeClientDTO convertToDTO(CommandeClient commande) {
        CommandeClientDTO dto = new CommandeClientDTO();
        dto.setId(commande.getId());
        dto.setClientId(commande.getClient() != null ? commande.getClient().getId() : null);
        dto.setDateCommande(commande.getDateCommande());
        dto.setStatut(commande.getStatut().toString());

        List<LigneCommandeDTO> lignes = (commande.getLignesCommande() == null ? List.<LigneCommande>of()
                : commande.getLignesCommande())
                .stream()
                .map(this::convertLigneToDTO)
                .collect(Collectors.toList());
        dto.setLignesCommande(lignes);

        double total = lignes.stream()
                .mapToDouble(LigneCommandeDTO::getMontantTotal)
                .sum();
        dto.setMontantTotal(total);

        return dto;
    }

    private LigneCommandeDTO convertLigneToDTO(LigneCommande ligne) {
        LigneCommandeDTO dto = new LigneCommandeDTO();
        dto.setId(ligne.getId());
        dto.setProduitId(ligne.getProduit().getId());
        dto.setProduitNom(ligne.getProduit().getNom());
        dto.setQuantite(ligne.getQuantite());
        dto.setPrixUnitaire(ligne.getPrixUnitaire());
        dto.setMontantTotal(ligne.getMontantTotal());
        return dto;
    }

    private CommandeClient convertToEntity(CommandeClientDTO dto) {
        CommandeClient commande = new CommandeClient();
        if (dto.getId() != null) {
            commande.setId(dto.getId());
        }
        if (dto.getStatut() != null) {
            try {
                commande.setStatut(CommandeClient.StatutCommande.valueOf(dto.getStatut()));
            } catch (IllegalArgumentException e) {
                commande.setStatut(CommandeClient.StatutCommande.en_attente);
            }
        }
        commande.setDateCommande(dto.getDateCommande() != null ? dto.getDateCommande() : LocalDateTime.now());
        return commande;
    }

    public CommandeClient createCommandeFromRequest(CommandeClientRequest request) {
        // Créer et persister le client d'abord
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setNom(request.getClient().getNom());
        client.setPrenom(request.getClient().getPrenom());
        client.setUsername(
                request.getClient().getNom().toLowerCase() + "." + request.getClient().getPrenom().toLowerCase());
        client.setTelephone(""); // valeur par défaut
        client.setAdresse(""); // valeur par défaut
        client.setMotDePasse(""); // valeur par défaut

        Client savedClient = clientRepository.save(client);

        // Créer la commande avec le client persisté
        CommandeClient commande = new CommandeClient();
        commande.setClient(savedClient);
        commande.setDateCommande(LocalDateTime.now());
        commande.setSeuilMax(request.getSeuilMax());

        // Gérer le statut
        if (request.getStatut() != null && !request.getStatut().isEmpty()) {
            try {
                commande.setStatut(CommandeClient.StatutCommande.valueOf(request.getStatut()));
            } catch (IllegalArgumentException e) {
                commande.setStatut(CommandeClient.StatutCommande.en_attente);
            }
        } else {
            commande.setStatut(CommandeClient.StatutCommande.en_attente);
        }

        return commandeClientRepository.save(commande);
    }
}
