package gestiondestock.model;

import javafx.beans.property.*;

public class Produit {
    private final StringProperty id;
    private final StringProperty nom;
    private final StringProperty description;
    private final DoubleProperty prixUnitaire;
    private final StringProperty urlImage;
    private Categorie categorie;
    private Fournisseur fournisseur;

    public Produit() {
        this.id = new SimpleStringProperty();
        this.nom = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.prixUnitaire = new SimpleDoubleProperty();
        this.urlImage = new SimpleStringProperty();
    }

    public Produit(String nom, String description, Double prixUnitaire, String urlImage, Categorie categorie, Fournisseur fournisseur) {
        this.id = new SimpleStringProperty();
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire != null ? prixUnitaire : 0.0);
        this.urlImage = new SimpleStringProperty(urlImage);
        this.categorie = categorie;
        this.fournisseur = fournisseur;
    }

    // Properties
    public StringProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }
    public StringProperty urlImageProperty() { return urlImage; }

    // Getters and Setters
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    public Double getPrixUnitaire() { return prixUnitaire.get(); }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire.set(prixUnitaire); }

    public String getUrlImage() { return urlImage.get(); }
    public void setUrlImage(String urlImage) { this.urlImage.set(urlImage); }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }
}