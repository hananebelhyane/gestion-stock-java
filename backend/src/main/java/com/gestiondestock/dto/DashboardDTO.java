package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private StatistiquesGeneralesDTO statistiquesGenerales;
    private StatistiquesVentesDTO statistiquesVentes;
    private StatistiquesStockDTO statistiquesStock;
    private StatistiquesCommandesDTO statistiquesCommandes;
    private EvolutionVentesDTO evolutionVentes;

    //C’est le DTO global du dashboard admin.
    //sert à :
    //Regrouper toutes les statistiques dans un seul objet
    //Renvoyer une réponse complète au frontend (JSON)

}
