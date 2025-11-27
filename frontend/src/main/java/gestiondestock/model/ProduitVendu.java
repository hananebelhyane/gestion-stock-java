package gestiondestock.model;

import javafx.beans.property.*;

public class ProduitVendu {

    private StringProperty produitId;
    private StringProperty nomProduit;
    private IntegerProperty quantiteVendue;
    private DoubleProperty montantTotal;

    public ProduitVendu() {
        this.produitId = new SimpleStringProperty();
        this.nomProduit = new SimpleStringProperty();
        this.quantiteVendue = new SimpleIntegerProperty();
        this.montantTotal = new SimpleDoubleProperty();
    }

    // Getters & Properties
    public String getProduitId() { return produitId.get(); }
    public void setProduitId(String produitId) { this.produitId.set(produitId); }
    public StringProperty produitIdProperty() { return produitId; }

    public String getNomProduit() { return nomProduit.get(); }
    public void setNomProduit(String nomProduit) { this.nomProduit.set(nomProduit); }
    public StringProperty nomProduitProperty() { return nomProduit; }

    public int getQuantiteVendue() { return quantiteVendue.get(); }
    public void setQuantiteVendue(int quantiteVendue) { this.quantiteVendue.set(quantiteVendue); }
    public IntegerProperty quantiteVendueProperty() { return quantiteVendue; }

    public double getMontantTotal() { return montantTotal.get(); }
    public void setMontantTotal(double montantTotal) { this.montantTotal.set(montantTotal); }
    public DoubleProperty montantTotalProperty() { return montantTotal; }
}
