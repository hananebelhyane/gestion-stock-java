package gestiondestock.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StatistiquesVentes {

    private DoubleProperty chiffreAffairesTotal;
    private DoubleProperty chiffreAffairesMois;
    private DoubleProperty chiffreAffairesAnnee;
    private IntegerProperty nombreCommandesMois;
    private IntegerProperty nombreCommandesEnAttente;
    private IntegerProperty nombreCommandesConfirmees;
    private IntegerProperty nombreCommandesAnnulees;

    private ObservableList<ProduitStock> topProduitsVendus;
    private ObservableList<ClientTop> topClients;

    public StatistiquesVentes() {
        this.chiffreAffairesTotal = new SimpleDoubleProperty();
        this.chiffreAffairesMois = new SimpleDoubleProperty();
        this.chiffreAffairesAnnee = new SimpleDoubleProperty();
        this.nombreCommandesMois = new SimpleIntegerProperty();
        this.nombreCommandesEnAttente = new SimpleIntegerProperty();
        this.nombreCommandesConfirmees = new SimpleIntegerProperty();
        this.nombreCommandesAnnulees = new SimpleIntegerProperty();
        this.topProduitsVendus = FXCollections.observableArrayList();
        this.topClients = FXCollections.observableArrayList();
    }

    // Getters & Properties
    public double getChiffreAffairesTotal() { return chiffreAffairesTotal.get(); }
    public void setChiffreAffairesTotal(double value) { chiffreAffairesTotal.set(value); }
    public DoubleProperty chiffreAffairesTotalProperty() { return chiffreAffairesTotal; }

    public double getChiffreAffairesMois() { return chiffreAffairesMois.get(); }
    public void setChiffreAffairesMois(double value) { chiffreAffairesMois.set(value); }
    public DoubleProperty chiffreAffairesMoisProperty() { return chiffreAffairesMois; }

    public double getChiffreAffairesAnnee() { return chiffreAffairesAnnee.get(); }
    public void setChiffreAffairesAnnee(double value) { chiffreAffairesAnnee.set(value); }
    public DoubleProperty chiffreAffairesAnneeProperty() { return chiffreAffairesAnnee; }

    public int getNombreCommandesMois() { return nombreCommandesMois.get(); }
    public void setNombreCommandesMois(int value) { nombreCommandesMois.set(value); }
    public IntegerProperty nombreCommandesMoisProperty() { return nombreCommandesMois; }

    public int getNombreCommandesEnAttente() { return nombreCommandesEnAttente.get(); }
    public void setNombreCommandesEnAttente(int value) { nombreCommandesEnAttente.set(value); }
    public IntegerProperty nombreCommandesEnAttenteProperty() { return nombreCommandesEnAttente; }

    public int getNombreCommandesConfirmees() { return nombreCommandesConfirmees.get(); }
    public void setNombreCommandesConfirmees(int value) { nombreCommandesConfirmees.set(value); }
    public IntegerProperty nombreCommandesConfirmeesProperty() { return nombreCommandesConfirmees; }

    public int getNombreCommandesAnnulees() { return nombreCommandesAnnulees.get(); }
    public void setNombreCommandesAnnulees(int value) { nombreCommandesAnnulees.set(value); }
    public IntegerProperty nombreCommandesAnnuleesProperty() { return nombreCommandesAnnulees; }

    public ObservableList<ProduitStock> getTopProduitsVendus() { return topProduitsVendus; }
    public ObservableList<ClientTop> getTopClients() { return topClients; }
}
