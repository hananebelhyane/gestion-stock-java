package gestiondestock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntreeStock {
    
    private UUID id;
    private UUID produitId;
    private String produitNom;
    private String produitReference;
    private Integer quantite;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateEntree;
    
    private UUID magasinierId;
    private String magasinierNom;
    private UUID commandeFournisseurId;
    private String commandeFournisseurReference;

    public EntreeStock() {}

    public EntreeStock(UUID produitId, Integer quantite, UUID commandeFournisseurId) {
        this.produitId = produitId;
        this.quantite = quantite;
        this.commandeFournisseurId = commandeFournisseurId;
    }

    public EntreeStock(UUID id, UUID produitId, String produitNom, String produitReference, 
                      Integer quantite, LocalDateTime dateEntree, UUID magasinierId, 
                      String magasinierNom, UUID commandeFournisseurId, 
                      String commandeFournisseurReference) {
        this.id = id;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.produitReference = produitReference;
        this.quantite = quantite;
        this.dateEntree = dateEntree;
        this.magasinierId = magasinierId;
        this.magasinierNom = magasinierNom;
        this.commandeFournisseurId = commandeFournisseurId;
        this.commandeFournisseurReference = commandeFournisseurReference;
    }

    // Getters et Setters
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

    public LocalDateTime getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDateTime dateEntree) {
        this.dateEntree = dateEntree;
    }

    public UUID getMagasinierId() {
        return magasinierId;
    }

    public void setMagasinierId(UUID magasinierId) {
        this.magasinierId = magasinierId;
    }

    public String getMagasinierNom() {
        return magasinierNom;
    }

    public void setMagasinierNom(String magasinierNom) {
        this.magasinierNom = magasinierNom;
    }

    public UUID getCommandeFournisseurId() {
        return commandeFournisseurId;
    }

    public void setCommandeFournisseurId(UUID commandeFournisseurId) {
        this.commandeFournisseurId = commandeFournisseurId;
    }

    public String getCommandeFournisseurReference() {
        return commandeFournisseurReference;
    }

    public void setCommandeFournisseurReference(String commandeFournisseurReference) {
        this.commandeFournisseurReference = commandeFournisseurReference;
    }
}