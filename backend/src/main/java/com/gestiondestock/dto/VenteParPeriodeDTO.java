package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteParPeriodeDTO {
    private String periode;
    private Double montant;
    private Long nombreCommandes;

    //Représente une valeur d’évolution (un point sur le graphique).
    //Sert à :
    //Définir le chiffre d’affaires ou le nombre de commandes
    //Pour une période donnée (jour, mois, année)
    //
    // Utilisé dans EvolutionVentesDTO.
}
