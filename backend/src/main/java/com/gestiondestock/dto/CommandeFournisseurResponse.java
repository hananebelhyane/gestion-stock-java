package com.gestiondestock.dto;

import com.gestiondestock.entity.CommandeFournisseur;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommandeFournisseurResponse {
    private UUID id;
    private ProduitSummary produit;
    private LocalDateTime commandeDate;
    private CommandeFournisseur.StatutCommande statut;

    @Data
    public static class ProduitSummary {
        private UUID id;
        private String nom;
        private String description;
        private Double prixUnitaire;
        private String urlImage;
    }
}
