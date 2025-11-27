package com.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitStockDTO {
    private UUID produitId;
    private String nomProduit;
    private Integer quantiteDisponible;
    private Integer seuilAlerte;
    private Double prixUnitaire;
    private String categorie;
    private String urlImage;

    //Représente un produit avec ses informations de stock.
    //POUR Afficher dans les tableaux :
    //Quantité restante
    //Seuil d’alerte
    //Catégorie
    //Prix et image
    //Utilisé dans StatistiquesStockDTO.
}
