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
public class SortieStockDTO {
    
    private UUID id;
    private UUID produitId;
    private String produitNom;
    private String produitReference;
    private Integer quantite;
    private LocalDateTime dateSortie;
    private UUID magasinierId;
    private String magasinierNom;
    private UUID ligneCommandeId;
    private String ligneCommandeReference;
    
    // Constructeur pour la création (sans ID)
    public SortieStockDTO(UUID produitId, Integer quantite, UUID ligneCommandeId) {
        this.produitId = produitId;
        this.quantite = quantite;
        this.ligneCommandeId = ligneCommandeId;
    }
}