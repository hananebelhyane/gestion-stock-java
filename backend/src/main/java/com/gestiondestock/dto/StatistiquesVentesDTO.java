package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesVentesDTO {

    private Double chiffreAffairesTotal;
    private Double chiffreAffairesMois;
    private Double chiffreAffairesAnnee;
    private Long nombreCommandesMois;
    private Long nombreCommandesEnAttente;
    private Long nombreCommandesConfirmees;
    private Long nombreCommandesAnnulees;
    private List<ProduitVenduDTO> topProduitsVendus;
    private List<ClientTopDTO> topClients;

    //Représente toutes les statistiques liées aux ventes et commandes.
    //C’est le DTO du “module ventes” dans ton dashboard (graphiques + tableaux).
}
