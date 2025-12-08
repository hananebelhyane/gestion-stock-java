package gestiondestock.model;

import javafx.beans.property.*;

public class ProduitStock {

    private StringProperty produitId;
    private StringProperty nomProduit;
    private IntegerProperty quantiteDisponible;
    private IntegerProperty seuilAlerte;
    private DoubleProperty prixUnitaire;
    private StringProperty categorie;
    private StringProperty urlImage;

    public ProduitStock() {
        this.produitId = new SimpleStringProperty();
        this.nomProduit = new SimpleStringProperty();
        this.quantiteDisponible = new SimpleIntegerProperty();
        this.seuilAlerte = new SimpleIntegerProperty();
        this.prixUnitaire = new SimpleDoubleProperty();
        this.categorie = new SimpleStringProperty();
        this.urlImage = new SimpleStringProperty();
    }

    // Getters & Setters
    public String getProduitId() { return produitId.get(); }
    public void setProduitId(String produitId) { this.produitId.set(produitId); }
    public StringProperty produitIdProperty() { return produitId; }

    public String getNomProduit() { return nomProduit.get(); }
    public void setNomProduit(String nomProduit) { this.nomProduit.set(nomProduit); }
    public StringProperty nomProduitProperty() { return nomProduit; }

    public int getQuantiteDisponible() { return quantiteDisponible.get(); }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible.set(quantiteDisponible); }
    public IntegerProperty quantiteDisponibleProperty() { return quantiteDisponible; }

    public int getSeuilAlerte() { return seuilAlerte.get(); }
    public void setSeuilAlerte(int seuilAlerte) { this.seuilAlerte.set(seuilAlerte); }
    public IntegerProperty seuilAlerteProperty() { return seuilAlerte; }

    public double getPrixUnitaire() { return prixUnitaire.get(); }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire.set(prixUnitaire); }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }

    public String getCategorie() { return categorie.get(); }
    public void setCategorie(String categorie) { this.categorie.set(categorie); }
    public StringProperty categorieProperty() { return categorie; }

    public String getUrlImage() { return urlImage.get(); }
    public void setUrlImage(String urlImage) { this.urlImage.set(urlImage); }
    public StringProperty urlImageProperty() { return urlImage; }
}
