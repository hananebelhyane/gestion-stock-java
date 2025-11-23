package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@Entity
@EntityListeners(EntityIdGenerator.class)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Produit {

    @Id
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("description")
    private String description;

    @JsonProperty("prixUnitaire")
    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    @JsonProperty("urlImage")
    @Column(name = "url_image")
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @JsonProperty("categorie")
    @JsonIgnoreProperties({"produits"})
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    @JsonProperty("fournisseur")
    @JsonIgnoreProperties({"produits", "deleted_by", "deleted_at"})
    private Fournisseur fournisseur;
}
