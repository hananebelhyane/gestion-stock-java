package com.gestiondestock.service;

import com.gestiondestock.dto.FactureDTO;
import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.Facture;
import com.gestiondestock.repository.CommandeClientRepository;
import com.gestiondestock.repository.FactureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;
    private final CommandeClientRepository commandeClientRepository;

    public FactureDTO generateFacture(UUID commandeId) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        // Vérifier si une facture existe déjà pour cette commande
        Optional<Facture> existing = factureRepository.findByCommandeId(commandeId);
        if (existing.isPresent()) {
            return convertToDTO(existing.get());
        }

        // Créer une nouvelle facture
        Facture facture = new Facture();
        facture.setCommande(commande);
        facture.setDateFacture(LocalDateTime.now());
        facture.setMontantTotal(commande.calculerMontantTotal());
        facture.setEstPayee(false);

        Facture saved = factureRepository.save(facture);
        return convertToDTO(saved);
    }

    public Optional<FactureDTO> getFactureByCommandeId(UUID commandeId) {
        return factureRepository.findByCommandeId(commandeId).map(this::convertToDTO);
    }

    public List<FactureDTO> getFacturesByClientId(UUID clientId) {
        return factureRepository.findByClientId(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FactureDTO> getFacturesByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return factureRepository.findByDateRange(debut, fin)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FactureDTO> getUnpaidFactures() {
        return factureRepository.findUnpaidFactures()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FactureDTO markAsPaid(UUID factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture not found"));

        facture.setEstPayee(true);
        Facture saved = factureRepository.save(facture);
        return convertToDTO(saved);
    }

    private FactureDTO convertToDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setCommandeId(facture.getCommande().getId());
        dto.setClientId(facture.getCommande().getClient().getId());
        dto.setDateFacture(facture.getDateFacture());
        dto.setMontantTotal(facture.getMontantTotal());
        dto.setEstPayee(facture.isEstPayee());
        return dto;
    }
}
