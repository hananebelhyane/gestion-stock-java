package gestiondestock.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClientModel {
    private UUID id;
    private String nom;
    private String prenom;
    private String username;
    private String telephone;
    private String adresse;
    private UUID deleted_by;
    private LocalDateTime deleted_at;

    public ClientModel() {}

    public ClientModel(UUID id, String nom, String prenom, String username, 
                      String telephone, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.username = username;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    // Getters et Setters
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

    public UUID getDeleted_by() { return deleted_by; }
    public void setDeleted_by(UUID deleted_by) { this.deleted_by = deleted_by; }

    public LocalDateTime getDeleted_at() { return deleted_at; }
    public void setDeleted_at(LocalDateTime deleted_at) { this.deleted_at = deleted_at; }
}