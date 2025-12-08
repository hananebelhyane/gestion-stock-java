package gestiondestock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SortieStock {
    
    private UUID id;
    private UUID produitId;
    private String produitNom;
    private String produitReference;
    private Integer quantite;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateSortie;
    
    private UUID magasinierId;
    private String magasinierNom;
    private UUID ligneCommandeId;
    private String ligneCommandeReference;

    public SortieStock() {}

    public SortieStock(UUID produitId, Integer quantite, UUID ligneCommandeId) {
        this.produitId = produitId;
        this.quantite = quantite;
        this.ligneCommandeId = ligneCommandeId;
    }

    public SortieStock(UUID id, UUID produitId, String produitNom, String produitReference, 
                      Integer quantite, LocalDateTime dateSortie, UUID magasinierId, 
                      String magasinierNom, UUID ligneCommandeId, 
                      String ligneCommandeReference) {
        this.id = id;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.produitReference = produitReference;
        this.quantite = quantite;
        this.dateSortie = dateSortie;
        this.magasinierId = magasinierId;
        this.magasinierNom = magasinierNom;
        this.ligneCommandeId = ligneCommandeId;
        this.ligneCommandeReference = ligneCommandeReference;
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

    public LocalDateTime getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDateTime dateSortie) {
        this.dateSortie = dateSortie;
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

    public UUID getLigneCommandeId() {
        return ligneCommandeId;
    }

    public void setLigneCommandeId(UUID ligneCommandeId) {
        this.ligneCommandeId = ligneCommandeId;
    }

    public String getLigneCommandeReference() {
        return ligneCommandeReference;
    }

    public void setLigneCommandeReference(String ligneCommandeReference) {
        this.ligneCommandeReference = ligneCommandeReference;
    }
}