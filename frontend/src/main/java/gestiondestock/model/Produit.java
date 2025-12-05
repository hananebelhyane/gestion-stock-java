package gestiondestock.model;

import java.util.UUID;

public class Produit {

    private UUID id;
    private String nom;
    private String description;
    private Double prixUnitaire;
    private String urlImage;
    private Categorie categorie;
    private Fournisseur fournisseur;

    public Produit() {
    }

    public Produit(UUID id, String nom, String description,
            Double prixUnitaire, String urlImage,
            Categorie categorie, Fournisseur fournisseur) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.urlImage = urlImage;
        this.categorie = categorie;
        this.fournisseur = fournisseur;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
}
