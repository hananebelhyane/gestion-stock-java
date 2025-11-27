package gestiondestock.model;

import javafx.beans.property.*;

public class VenteParPeriode {

    private StringProperty periode;
    private DoubleProperty montant;
    private IntegerProperty nombreCommandes;

    public VenteParPeriode() {
        this.periode = new SimpleStringProperty();
        this.montant = new SimpleDoubleProperty();
        this.nombreCommandes = new SimpleIntegerProperty();
    }

    // Getters & Setters
    public String getPeriode() { return periode.get(); }
    public void setPeriode(String periode) { this.periode.set(periode); }
    public StringProperty periodeProperty() { return periode; }

    public double getMontant() { return montant.get(); }
    public void setMontant(double montant) { this.montant.set(montant); }
    public DoubleProperty montantProperty() { return montant; }

    public int getNombreCommandes() { return nombreCommandes.get(); }
    public void setNombreCommandes(int nombreCommandes) { this.nombreCommandes.set(nombreCommandes); }
    public IntegerProperty nombreCommandesProperty() { return nombreCommandes; }
}
