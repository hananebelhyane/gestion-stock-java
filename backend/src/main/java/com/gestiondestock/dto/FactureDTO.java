package com.gestiondestock.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class FactureDTO {
    private UUID id;
    private UUID commandeId;
    private UUID clientId;
    private LocalDateTime dateFacture;
    private Double montantTotal;
    private boolean estPayee;
    private CommandeClientDTO commande;

    public FactureDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(UUID commandeId) {
        this.commandeId = commandeId;
    }

    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDateTime dateFacture) {
        this.dateFacture = dateFacture;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public boolean isEstPayee() {
        return estPayee;
    }

    public void setEstPayee(boolean estPayee) {
        this.estPayee = estPayee;
    }

    public CommandeClientDTO getCommande() {
        return commande;
    }

    public void setCommande(CommandeClientDTO commande) {
        this.commande = commande;
    }
}
