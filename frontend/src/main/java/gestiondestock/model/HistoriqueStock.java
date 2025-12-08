package gestiondestock.model;

import java.time.LocalDateTime;

/**
 * Classe helper pour afficher l'historique unifié (entrées + sorties)
 */
public class HistoriqueStock {
    
    private String type; // "ENTREE" ou "SORTIE"
    private String produitNom;
    private Integer quantite;
    private LocalDateTime date;
    private String magasinierNom;

    public HistoriqueStock() {}

    public HistoriqueStock(String type, String produitNom, Integer quantite, 
                          LocalDateTime date, String magasinierNom) {
        this.type = type;
        this.produitNom = produitNom;
        this.quantite = quantite;
        this.date = date;
        this.magasinierNom = magasinierNom;
    }

    // Getters et Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMagasinierNom() {
        return magasinierNom;
    }

    public void setMagasinierNom(String magasinierNom) {
        this.magasinierNom = magasinierNom;
    }
}