package gestiondestock.model;

import java.util.UUID;

public class Stock {

    private UUID id;
    private Produit produit;
    private Integer quantiteDisponible;
    private Integer seuilAlerte;

    public Stock() {
    }

    public Stock(UUID id, Produit produit, Integer quantiteDisponible, Integer seuilAlerte) {
        this.id = id;
        this.produit = produit;
        this.quantiteDisponible = quantiteDisponible;
        this.seuilAlerte = seuilAlerte;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Integer getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public void setQuantiteDisponible(Integer quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }

    public Integer getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(Integer seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
}
