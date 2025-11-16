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
        this();
        this.nom.set(nom);
        this.description.set(description);
        this.prixUnitaire.set(prixUnitaire != null ? prixUnitaire : 0.0);
        this.urlImage.set(urlImage);
        this.categorie = categorie;
        this.fournisseur = fournisseur;
    }

    // Properties
    public StringProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }
    public StringProperty urlImageProperty() { return urlImage; }

    // Getters et setters
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

    // Méthode pour obtenir un POJO simple pour Gson
    public ProduitDTO toDTO() {
        ProduitDTO dto = new ProduitDTO();
        dto.setId(getId());
        dto.setNom(getNom());
        dto.setDescription(getDescription());
        dto.setPrixUnitaire(getPrixUnitaire());
        dto.setUrlImage(getUrlImage());
        dto.setCategorie(categorie);
        dto.setFournisseur(fournisseur);
        return dto;
    }

    // Méthode pour remplir Produit à partir de DTO
    public static Produit fromDTO(ProduitDTO dto) {
        Produit p = new Produit(
                dto.getNom(),
                dto.getDescription(),
                dto.getPrixUnitaire(),
                dto.getUrlImage(),
                dto.getCategorie(),
                dto.getFournisseur()
        );
        p.setId(dto.getId());
        return p;
    }
}
