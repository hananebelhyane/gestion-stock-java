package gestiondestock.dto;

import java.util.List;

public class StatistiquesVentesDTO {

    private double chiffreAffairesTotal;
    private double chiffreAffairesMois;
    private double chiffreAffairesAnnee;
    private int nombreCommandesMois;
    private int nombreCommandesEnAttente;
    private int nombreCommandesConfirmees;
    private int nombreCommandesAnnulees;

    private List<ProduitVenduDTO> topProduitsVendus;
    private List<ClientTopDTO> topClients;

    // --- Getters & Setters ---
    public double getChiffreAffairesTotal() {
        return chiffreAffairesTotal;
    }

    public void setChiffreAffairesTotal(double chiffreAffairesTotal) {
        this.chiffreAffairesTotal = chiffreAffairesTotal;
    }

    public double getChiffreAffairesMois() {
        return chiffreAffairesMois;
    }

    public void setChiffreAffairesMois(double chiffreAffairesMois) {
        this.chiffreAffairesMois = chiffreAffairesMois;
    }

    public double getChiffreAffairesAnnee() {
        return chiffreAffairesAnnee;
    }

    public void setChiffreAffairesAnnee(double chiffreAffairesAnnee) {
        this.chiffreAffairesAnnee = chiffreAffairesAnnee;
    }

    public int getNombreCommandesMois() {
        return nombreCommandesMois;
    }

    public void setNombreCommandesMois(int nombreCommandesMois) {
        this.nombreCommandesMois = nombreCommandesMois;
    }

    public int getNombreCommandesEnAttente() {
        return nombreCommandesEnAttente;
    }

    public void setNombreCommandesEnAttente(int nombreCommandesEnAttente) {
        this.nombreCommandesEnAttente = nombreCommandesEnAttente;
    }

    public int getNombreCommandesConfirmees() {
        return nombreCommandesConfirmees;
    }

    public void setNombreCommandesConfirmees(int nombreCommandesConfirmees) {
        this.nombreCommandesConfirmees = nombreCommandesConfirmees;
    }

    public int getNombreCommandesAnnulees() {
        return nombreCommandesAnnulees;
    }

    public void setNombreCommandesAnnulees(int nombreCommandesAnnulees) {
        this.nombreCommandesAnnulees = nombreCommandesAnnulees;
    }

    public List<ProduitVenduDTO> getTopProduitsVendus() {
        return topProduitsVendus;
    }

    public void setTopProduitsVendus(List<ProduitVenduDTO> topProduitsVendus) {
        this.topProduitsVendus = topProduitsVendus;
    }

    public List<ClientTopDTO> getTopClients() {
        return topClients;
    }

    public void setTopClients(List<ClientTopDTO> topClients) {
        this.topClients = topClients;
    }
}
