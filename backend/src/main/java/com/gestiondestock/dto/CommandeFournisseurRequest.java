package com.gestiondestock.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurRequest {

    private ProduitInfo produit;
    private String statut;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProduitInfo {

        private String nom;
        private String description;
    }
}
