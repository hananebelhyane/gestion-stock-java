package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntreeStockDTO {
    
    private UUID id;
    private UUID produitId;
    private String produitNom;
    private String produitReference;
    private Integer quantite;
    private LocalDateTime dateEntree;
    private UUID magasinierId;
    private String magasinierNom;
    private UUID commandeFournisseurId;
    private String commandeFournisseurReference;
    
    // Constructeur pour la création (sans ID)
    public EntreeStockDTO(UUID produitId, Integer quantite, UUID commandeFournisseurId) {
        this.produitId = produitId;
        this.quantite = quantite;
        this.commandeFournisseurId = commandeFournisseurId;
    }
}