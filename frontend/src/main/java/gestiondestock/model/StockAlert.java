package gestiondestock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockAlert {
    
    private UUID stockId;
    private UUID produitId;
    private String produitNom;
    private Integer quantiteDisponible;
    private Integer seuilAlerte;
    private String niveauAlerte; // "RUPTURE", "FAIBLE", "CRITIQUE"

    public StockAlert() {}

    public StockAlert(UUID stockId, UUID produitId, String produitNom, 
                     Integer quantiteDisponible, Integer seuilAlerte, String niveauAlerte) {
        this.stockId = stockId;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.quantiteDisponible = quantiteDisponible;
        this.seuilAlerte = seuilAlerte;
        this.niveauAlerte = niveauAlerte;
    }

    // Getters et Setters
    public UUID getStockId() {
        return stockId;
    }

    public void setStockId(UUID stockId) {
        this.stockId = stockId;
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

    public String getNiveauAlerte() {
        return niveauAlerte;
    }

    public void setNiveauAlerte(String niveauAlerte) {
        this.niveauAlerte = niveauAlerte;
    }
}