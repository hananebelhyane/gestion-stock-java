package com.gestiondestock.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for CommandeClient responses to avoid circular serialization issues
 */
public class CommandeClientResponse {

    private UUID id;
    private ClientInfo client;
    private LocalDateTime dateCommande;
    private String statut;
    private Integer seuilMax;

    public CommandeClientResponse() {
    }

    public CommandeClientResponse(UUID id, ClientInfo client, LocalDateTime dateCommande, String statut, Integer seuilMax) {
        this.id = id;
        this.client = client;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.seuilMax = seuilMax;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ClientInfo getClient() { return client; }
    public void setClient(ClientInfo client) { this.client = client; }

    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Integer getSeuilMax() { return seuilMax; }
    public void setSeuilMax(Integer seuilMax) { this.seuilMax = seuilMax; }

    public static class ClientInfo {
        private UUID id;
        private String nom;
        private String prenom;
        private String username;
        private String telephone;
        private String adresse;

        public ClientInfo() {
        }

        public ClientInfo(UUID id, String nom, String prenom, String username, String telephone, String adresse) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.username = username;
            this.telephone = telephone;
            this.adresse = adresse;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }

        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
    }
}
