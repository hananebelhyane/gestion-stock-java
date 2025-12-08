package gestiondestock.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StatistiquesStock {

    private IntegerProperty nombreProduitsTotal;
    private IntegerProperty nombreProduitsRupture;
    private IntegerProperty nombreProduitsStockFaible;
    private DoubleProperty valeurTotaleStock;

    private ObservableList<ProduitStock> produitsRupture;
    private ObservableList<ProduitStock> produitsStockFaible;

    public StatistiquesStock() {
        this.nombreProduitsTotal = new SimpleIntegerProperty();
        this.nombreProduitsRupture = new SimpleIntegerProperty();
        this.nombreProduitsStockFaible = new SimpleIntegerProperty();
        this.valeurTotaleStock = new SimpleDoubleProperty();
        this.produitsRupture = FXCollections.observableArrayList();
        this.produitsStockFaible = FXCollections.observableArrayList();
    }

    // Getters & Properties
    public int getNombreProduitsTotal() { return nombreProduitsTotal.get(); }
    public void setNombreProduitsTotal(int value) { nombreProduitsTotal.set(value); }
    public IntegerProperty nombreProduitsTotalProperty() { return nombreProduitsTotal; }

    public int getNombreProduitsRupture() { return nombreProduitsRupture.get(); }
    public void setNombreProduitsRupture(int value) { nombreProduitsRupture.set(value); }
    public IntegerProperty nombreProduitsRuptureProperty() { return nombreProduitsRupture; }

    public int getNombreProduitsStockFaible() { return nombreProduitsStockFaible.get(); }
    public void setNombreProduitsStockFaible(int value) { nombreProduitsStockFaible.set(value); }
    public IntegerProperty nombreProduitsStockFaibleProperty() { return nombreProduitsStockFaible; }

    public double getValeurTotaleStock() { return valeurTotaleStock.get(); }
    public void setValeurTotaleStock(double value) { valeurTotaleStock.set(value); }
    public DoubleProperty valeurTotaleStockProperty() { return valeurTotaleStock; }

    public ObservableList<ProduitStock> getProduitsRupture() { return produitsRupture; }
    public ObservableList<ProduitStock> getProduitsStockFaible() { return produitsStockFaible; }
}
