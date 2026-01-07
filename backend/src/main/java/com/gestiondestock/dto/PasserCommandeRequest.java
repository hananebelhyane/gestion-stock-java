package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasserCommandeRequest {

    // Infos client
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String username;
    // Produits commandés
    private List<LigneCommandeRequest> lignes;
}
