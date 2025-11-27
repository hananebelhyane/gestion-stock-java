package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionVentesDTO {
    private List<VenteParPeriodeDTO> ventesParJour;
    private List<VenteParPeriodeDTO> ventesParMois;
    private List<VenteParPeriodeDTO> ventesParAnnee;

    //Sert à tracer des graphiques d’évolution (revenus ou commandes dans le temps).
    //Générer les courbes journalières, mensuelles, annuelles sur le front-end (React, Chart.js…)
    // C’est le DTO des graphiques temporels du dashboard.
}
