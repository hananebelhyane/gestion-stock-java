package gestiondestock.model;

import javafx.beans.property.*;

public class StatistiquesCommandes {

    private IntegerProperty totalCommandes;
    private IntegerProperty commandesEnAttente;
    private IntegerProperty commandesConfirmees;
    private IntegerProperty commandesAnnulees;
    private DoubleProperty tauxConfirmation;
    private DoubleProperty tauxAnnulation;

    public StatistiquesCommandes() {
        this.totalCommandes = new SimpleIntegerProperty();
        this.commandesEnAttente = new SimpleIntegerProperty();
        this.commandesConfirmees = new SimpleIntegerProperty();
        this.commandesAnnulees = new SimpleIntegerProperty();
        this.tauxConfirmation = new SimpleDoubleProperty();
        this.tauxAnnulation = new SimpleDoubleProperty();
    }

    // Getters & Properties
    public int getTotalCommandes() { return totalCommandes.get(); }
    public void setTotalCommandes(int value) { totalCommandes.set(value); }
    public IntegerProperty totalCommandesProperty() { return totalCommandes; }

    public int getCommandesEnAttente() { return commandesEnAttente.get(); }
    public void setCommandesEnAttente(int value) { commandesEnAttente.set(value); }
    public IntegerProperty commandesEnAttenteProperty() { return commandesEnAttente; }

    public int getCommandesConfirmees() { return commandesConfirmees.get(); }
    public void setCommandesConfirmees(int value) { commandesConfirmees.set(value); }
    public IntegerProperty commandesConfirmeesProperty() { return commandesConfirmees; }

    public int getCommandesAnnulees() { return commandesAnnulees.get(); }
    public void setCommandesAnnulees(int value) { commandesAnnulees.set(value); }
    public IntegerProperty commandesAnnuleesProperty() { return commandesAnnulees; }

    public double getTauxConfirmation() { return tauxConfirmation.get(); }
    public void setTauxConfirmation(double value) { tauxConfirmation.set(value); }
    public DoubleProperty tauxConfirmationProperty() { return tauxConfirmation; }

    public double getTauxAnnulation() { return tauxAnnulation.get(); }
    public void setTauxAnnulation(double value) { tauxAnnulation.set(value); }
    public DoubleProperty tauxAnnulationProperty() { return tauxAnnulation; }
}
