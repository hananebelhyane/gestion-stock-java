package com.gestiondestock.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String nom;
    private String description;
    private Double prixUnitaire;
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
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
