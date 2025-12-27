package com.gestiondestock.dto;

import java.util.UUID;

public class LigneCommandeDTO {
    private UUID id;
    private UUID produitId;
    private String produitNom;
    private Integer quantite;
    private Double prixUnitaire;
    private Double montantTotal;

    public LigneCommandeDTO() {
    }

    public LigneCommandeDTO(UUID produitId, Integer quantite, Double prixUnitaire) {
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantTotal = quantite * prixUnitaire;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProduitId() {
        return produitId;
    }

    public void setProduitId(UUID produitId) {
        this.produitId = produitId;
    }

    public String getProduitNom() {
        return produitNom;
    }

    public void setProduitNom(String produitNom) {
        this.produitNom = produitNom;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }
}
