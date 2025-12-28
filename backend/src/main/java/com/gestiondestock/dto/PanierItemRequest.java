package com.gestiondestock.dto;

import java.util.UUID;

public class PanierItemRequest {
    private UUID produitId;
    /**
     * Quantity delta to apply (+1 adds one, -1 removes one).
     * If the resulting quantity is <= 0, the line is removed.
     */
    private int quantite;

    public UUID getProduitId() {
        return produitId;
    }

    public void setProduitId(UUID produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
