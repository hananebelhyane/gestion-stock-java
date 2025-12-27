package com.gestiondestock.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CommandeClientDTO {
    private UUID id;
    private UUID clientId;
    private String clientNom;
    private LocalDateTime dateCommande;
    private String statut;
    private Integer seuilMax;
    private List<LigneCommandeDTO> lignesCommande;
    private Double montantTotal;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getSeuilMax() {
        return seuilMax;
    }

    public void setSeuilMax(Integer seuilMax) {
        this.seuilMax = seuilMax;
    }

    public List<LigneCommandeDTO> getLignesCommande() {
        return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommandeDTO> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }
}
