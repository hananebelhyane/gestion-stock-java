package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitVenduDTO {
    private UUID produitId;
    private String nomProduit;
    private Long quantiteVendue;
    private String urlImage;

    //Représente un produit vendu (pour le classement des ventes).
    //Sert à  Afficher dans le top 5 ou top 10 :

    //Le nom du produit
    //
    //Sa quantité vendue
    //
    //Une image éventuelle

}
