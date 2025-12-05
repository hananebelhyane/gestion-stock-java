package gestiondestock.dto;

import java.util.List;

public class StatistiquesStockDTO {

    private int nombreProduitsTotal;
    private int nombreProduitsRupture;
    private int nombreProduitsStockFaible;
    private double valeurTotaleStock;

    private List<ProduitStockDTO> produitsRupture;
    private List<ProduitStockDTO> produitsStockFaible;

    // --- Getters & Setters ---
    public int getNombreProduitsTotal() {
        return nombreProduitsTotal;
    }

    public void setNombreProduitsTotal(int nombreProduitsTotal) {
        this.nombreProduitsTotal = nombreProduitsTotal;
    }

    public int getNombreProduitsRupture() {
        return nombreProduitsRupture;
    }

    public void setNombreProduitsRupture(int nombreProduitsRupture) {
        this.nombreProduitsRupture = nombreProduitsRupture;
    }

    public int getNombreProduitsStockFaible() {
        return nombreProduitsStockFaible;
    }

    public void setNombreProduitsStockFaible(int nombreProduitsStockFaible) {
        this.nombreProduitsStockFaible = nombreProduitsStockFaible;
    }

    public double getValeurTotaleStock() {
        return valeurTotaleStock;
    }

    public void setValeurTotaleStock(double valeurTotaleStock) {
        this.valeurTotaleStock = valeurTotaleStock;
    }

    public List<ProduitStockDTO> getProduitsRupture() {
        return produitsRupture;
    }

    public void setProduitsRupture(List<ProduitStockDTO> produitsRupture) {
        this.produitsRupture = produitsRupture;
    }

    public List<ProduitStockDTO> getProduitsStockFaible() {
        return produitsStockFaible;
    }

    public void setProduitsStockFaible(List<ProduitStockDTO> produitsStockFaible) {
        this.produitsStockFaible = produitsStockFaible;
    }
}
