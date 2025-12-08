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
public class MouvementStockDTO {
    
    private UUID id;
    private String type; // "ENTREE" ou "SORTIE"
    private UUID produitId;
    private String produitNom;
    private String produitReference;
    private Integer quantite;
    private LocalDateTime dateMouvement;
    private String magasinierNom;
    private String reference; // Référence commande ou bon
}