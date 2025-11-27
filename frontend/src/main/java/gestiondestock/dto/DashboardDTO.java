package gestiondestock.dto;

import java.util.List;

public class DashboardDTO {

    private StatistiquesGeneralesDTO statistiquesGenerales;
    private StatistiquesVentesDTO statistiquesVentes;
    private StatistiquesStockDTO statistiquesStock;
    private StatistiquesCommandesDTO statistiquesCommandes;
    private EvolutionVentesDTO evolutionVentes;

    private List<ProduitVenduDTO> topProduits;
    private List<ClientTopDTO> topClients;

    // --- Getters & Setters ---
    public StatistiquesGeneralesDTO getStatistiquesGenerales() {
        return statistiquesGenerales;
    }

    public void setStatistiquesGenerales(StatistiquesGeneralesDTO statistiquesGenerales) {
        this.statistiquesGenerales = statistiquesGenerales;
    }

    public StatistiquesVentesDTO getStatistiquesVentes() {
        return statistiquesVentes;
    }

    public void setStatistiquesVentes(StatistiquesVentesDTO statistiquesVentes) {
        this.statistiquesVentes = statistiquesVentes;
    }

    public StatistiquesStockDTO getStatistiquesStock() {
        return statistiquesStock;
    }

    public void setStatistiquesStock(StatistiquesStockDTO statistiquesStock) {
        this.statistiquesStock = statistiquesStock;
    }

    public StatistiquesCommandesDTO getStatistiquesCommandes() {
        return statistiquesCommandes;
    }

    public void setStatistiquesCommandes(StatistiquesCommandesDTO statistiquesCommandes) {
        this.statistiquesCommandes = statistiquesCommandes;
    }

    public EvolutionVentesDTO getEvolutionVentes() {
        return evolutionVentes;
    }

    public void setEvolutionVentes(EvolutionVentesDTO evolutionVentes) {
        this.evolutionVentes = evolutionVentes;
    }

    public List<ProduitVenduDTO> getTopProduits() {
        return topProduits;
    }

    public void setTopProduits(List<ProduitVenduDTO> topProduits) {
        this.topProduits = topProduits;
    }

    public List<ClientTopDTO> getTopClients() {
        return topClients;
    }

    public void setTopClients(List<ClientTopDTO> topClients) {
        this.topClients = topClients;
    }
}
