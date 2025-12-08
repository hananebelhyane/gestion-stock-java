package gestiondestock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlerteStock {
    
    private UUID id;
    private SimpleProduit produit;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAlerte;
    
    private String message;
    private String statut; // "NON_LU" ou "TRAITE"

    public AlerteStock() {}

    // Getters et Setters
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

    public LocalDateTime getDateAlerte() {
        return dateAlerte;
    }

    public void setDateAlerte(LocalDateTime dateAlerte) {
        this.dateAlerte = dateAlerte;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Helper methods
    public boolean isTraitee() {
        return "TRAITE".equals(statut);
    }

    public String getNiveauAlerte() {
        // Extraire le niveau depuis le message
        if (message != null) {
            if (message.contains("URGENT") || message.contains("Rupture")) {
                return "RUPTURE";
            } else if (message.contains("CRITIQUE") || message.contains("critique")) {
                return "CRITIQUE";
            } else if (message.contains("FAIBLE") || message.contains("faible")) {
                return "FAIBLE";
            }
        }
        return "FAIBLE";
    }

    /**
     * Simple POJO for product info
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimpleProduit {
        private UUID id;
        private String nom;
        private String reference;
        private Double prixUnitaire;

        public SimpleProduit() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        
        public Double getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }
}