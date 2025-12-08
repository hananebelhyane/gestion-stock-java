package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertDTO {
    
    private UUID stockId;
    private UUID produitId;
    private String produitNom;
    private Integer quantiteDisponible;
    private Integer seuilAlerte;
    private String niveauAlerte; // "RUPTURE", "FAIBLE", "CRITIQUE"
    
    // Constructeur personnalisé (SANS produitReference)
    public StockAlertDTO(UUID stockId, UUID produitId, String produitNom, 
                         Integer quantiteDisponible, Integer seuilAlerte) {
        this.stockId = stockId;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.quantiteDisponible = quantiteDisponible;
        this.seuilAlerte = seuilAlerte;
        this.niveauAlerte = determinerNiveauAlerte(quantiteDisponible, seuilAlerte);
    }
    
    private String determinerNiveauAlerte(Integer quantite, Integer seuil) {
        if (quantite == 0) {
            return "RUPTURE";
        } else if (quantite <= seuil / 2) {
            return "CRITIQUE";
        } else if (quantite <= seuil) {
            return "FAIBLE";
        }
        return "OK";
    }
}