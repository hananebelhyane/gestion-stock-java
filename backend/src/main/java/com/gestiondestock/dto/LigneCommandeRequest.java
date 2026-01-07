package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeRequest {
    private String produitId;
    private Integer quantite;
}
