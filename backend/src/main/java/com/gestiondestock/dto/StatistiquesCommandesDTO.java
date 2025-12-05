package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesCommandesDTO {
    private Long totalCommandes;
    private Long commandesEnAttente;
    private Long commandesConfirmees;
    private Long commandesAnnulees;
    private Double tauxConfirmation;
    private Double tauxAnnulation;

    //Donne des statistiques sur les commandes uniquement.

    // Sert à :
    //Afficher les pourcentages de :
    //Commandes confirmées
    //Commandes annulées
    //Commandes en attente
    // Pour un graphique en donut ou barres dans le dashboard

}
