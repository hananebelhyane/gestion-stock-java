package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesGeneralesDTO {
    private Long nombreTotalProduits;
    private Long nombreTotalClients;
    private Long nombreTotalCommandes;
    private Double valeurTotaleStock;
    private Long nombreProduitsRupture;
    private Long nombreAlertesNonLues;
    private Double chiffreAffairesTotal;


        //le résumé global du système (vue d’ensemble affichée en haut du dashboard admin).
    //Sert à Afficher des indicateurs rapides

    //C’est le DTO principal pour les “cartes de résumé” dans ton dashboard.
}
