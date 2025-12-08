package gestiondestock.dto;

public class StatistiquesGeneralesDTO {

    private int nombreTotalProduits;
    private int nombreTotalClients;
    private int nombreTotalCommandes;
    private double valeurTotaleStock;
    private int nombreProduitsRupture;
    private int nombreAlertesNonLues;
    private double chiffreAffairesTotal; // ← nouveau champ

    // --- Getters & Setters ---
    public int getNombreTotalProduits() {
        return nombreTotalProduits;
    }
    public void setNombreTotalProduits(int nombreTotalProduits) {
        this.nombreTotalProduits = nombreTotalProduits;
    }

    public int getNombreTotalClients() {
        return nombreTotalClients;
    }
    public void setNombreTotalClients(int nombreTotalClients) {
        this.nombreTotalClients = nombreTotalClients;
    }

    public int getNombreTotalCommandes() {
        return nombreTotalCommandes;
    }
    public void setNombreTotalCommandes(int nombreTotalCommandes) {
        this.nombreTotalCommandes = nombreTotalCommandes;
    }

    public double getValeurTotaleStock() {
        return valeurTotaleStock;
    }
    public void setValeurTotaleStock(double valeurTotaleStock) {
        this.valeurTotaleStock = valeurTotaleStock;
    }

    public int getNombreProduitsRupture() {
        return nombreProduitsRupture;
    }
    public void setNombreProduitsRupture(int nombreProduitsRupture) {
        this.nombreProduitsRupture = nombreProduitsRupture;
    }

    public int getNombreAlertesNonLues() {
        return nombreAlertesNonLues;
    }
    public void setNombreAlertesNonLues(int nombreAlertesNonLues) {
        this.nombreAlertesNonLues = nombreAlertesNonLues;
    }

    public double getChiffreAffairesTotal() { // ← getter manquant
        return chiffreAffairesTotal;
    }
    public void setChiffreAffairesTotal(double chiffreAffairesTotal) { // ← setter manquant
        this.chiffreAffairesTotal = chiffreAffairesTotal;
    }
}
