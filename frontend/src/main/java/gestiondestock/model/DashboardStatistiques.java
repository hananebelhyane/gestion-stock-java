package gestiondestock.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardStatistiques {

    private StatistiquesVentes statistiquesVentes;
    private StatistiquesStock statistiquesStock;
    private StatistiquesCommandes statistiquesCommandes;
    private EvolutionVentes evolutionVentes;

    public DashboardStatistiques() {
        this.statistiquesVentes = new StatistiquesVentes();
        this.statistiquesStock = new StatistiquesStock();
        this.statistiquesCommandes = new StatistiquesCommandes();
        this.evolutionVentes = new EvolutionVentes();
    }

    // Getters
    public StatistiquesVentes getStatistiquesVentes() { return statistiquesVentes; }
    public StatistiquesStock getStatistiquesStock() { return statistiquesStock; }
    public StatistiquesCommandes getStatistiquesCommandes() { return statistiquesCommandes; }
    public EvolutionVentes getEvolutionVentes() { return evolutionVentes; }
}
