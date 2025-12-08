package gestiondestock.model;

public class ProduitDTO {
    private String id;
    private String nom;
    private String description;
    private Double prixUnitaire;
    private String urlImage;
    private CategorieDTO categorie;
    private FournisseurDTO fournisseur;

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }

    public CategorieDTO getCategorie() { return categorie; }
    public void setCategorie(CategorieDTO categorie) { this.categorie = categorie; }

    public FournisseurDTO getFournisseur() { return fournisseur; }
    public void setFournisseur(FournisseurDTO fournisseur) { this.fournisseur = fournisseur; }
}
