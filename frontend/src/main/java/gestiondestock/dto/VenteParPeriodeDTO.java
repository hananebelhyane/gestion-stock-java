package gestiondestock.dto;

public class VenteParPeriodeDTO {

    private String periode; // yyyy-MM-dd ou yyyy-MM ou yyyy
    private double montant;
    private int nombreCommandes;

    // --- Getters & Setters ---
    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public int getNombreCommandes() {
        return nombreCommandes;
    }

    public void setNombreCommandes(int nombreCommandes) {
        this.nombreCommandes = nombreCommandes;
    }
}
