package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


public class ClientTopDTO {
    private UUID clientId;
    private String nom;
    private String prenom;
    private Double totalAchats;
    private Long nombreCommandes;
    //Afficher les meilleurs clients avec :
    //
    //Leur total d’achats
    //
    //Leur nombre de commandes


}
