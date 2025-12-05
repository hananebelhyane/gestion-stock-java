package gestiondestock.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EvolutionVentes {

    private ObservableList<VenteParPeriode> ventesParJour;
    private ObservableList<VenteParPeriode> ventesParMois;
    private ObservableList<VenteParPeriode> ventesParAnnee;

    public EvolutionVentes() {
        this.ventesParJour = FXCollections.observableArrayList();
        this.ventesParMois = FXCollections.observableArrayList();
        this.ventesParAnnee = FXCollections.observableArrayList();
    }

    // Getters
    public ObservableList<VenteParPeriode> getVentesParJour() { return ventesParJour; }
    public ObservableList<VenteParPeriode> getVentesParMois() { return ventesParMois; }
    public ObservableList<VenteParPeriode> getVentesParAnnee() { return ventesParAnnee; }
}
