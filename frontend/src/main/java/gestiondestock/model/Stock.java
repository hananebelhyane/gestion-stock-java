package gestiondestock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {

    private UUID id;
    private SimpleProduit produit;
    private Integer quantiteDisponible;
    private Integer seuilAlerte;

    public Stock() {
    }

    public Stock(UUID id, SimpleProduit produit, Integer quantiteDisponible, Integer seuilAlerte) {
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

    public SimpleProduit getProduit() {
        return produit;
    }

    public void setProduit(SimpleProduit produit) {
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

    /**
     * Simple POJO for product info in Stock context (no JavaFX properties)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimpleProduit {
        private UUID id;
        private String nom;
        private String description;
        private Double prixUnitaire;
        private String urlImage;

        public SimpleProduit() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
        
        public String getUrlImage() { return urlImage; }
        public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    }
}
