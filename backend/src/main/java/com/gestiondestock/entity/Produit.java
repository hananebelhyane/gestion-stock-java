package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

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

    // Getters
    public UUID getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
}
