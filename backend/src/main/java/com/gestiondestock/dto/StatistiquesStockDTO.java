package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesStockDTO {
    private Long nombreProduitsTotal;
    private Long nombreProduitsRupture;
    private Long nombreProduitsStockFaible;
    private Double valeurTotaleStock;
    private List<ProduitStockDTO> produitsRupture;
    private List<ProduitStockDTO> produitsStockFaible;

    //Afficher les indicateurs stock
    //Lister les produits :
    //en rupture
    //proches du seuil d’alerte
    //Calculer la valeur totale du stock

    //Utile pour la section "Suivi du stock" du dashboard.

}
