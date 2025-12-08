package gestiondestock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MouvementStock {
    
    private UUID id;
    private String type; // "ENTREE" ou "SORTIE"
    private UUID produitId;
    private String produitNom;
    private String produitReference;
    private Integer quantite;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateMouvement;
    
    private String magasinierNom;
    private String reference; // Référence commande ou bon

    public MouvementStock() {}

    public MouvementStock(UUID id, String type, UUID produitId, String produitNom, 
                         String produitReference, Integer quantite, LocalDateTime dateMouvement, 
                         String magasinierNom, String reference) {
        this.id = id;
        this.type = type;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.produitReference = produitReference;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.magasinierNom = magasinierNom;
        this.reference = reference;
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getProduitReference() {
        return produitReference;
    }

    public void setProduitReference(String produitReference) {
        this.produitReference = produitReference;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getMagasinierNom() {
        return magasinierNom;
    }

    public void setMagasinierNom(String magasinierNom) {
        this.magasinierNom = magasinierNom;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}