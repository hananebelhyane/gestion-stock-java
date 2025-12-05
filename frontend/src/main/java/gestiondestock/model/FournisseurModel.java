package gestiondestock.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class FournisseurModel {
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private UUID deleted_by;
    private LocalDateTime deleted_at;

    public FournisseurModel() {}

    public FournisseurModel(UUID id, String nom, String prenom, String email, String telephone, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public UUID getDeleted_by() { return deleted_by; }
    public void setDeleted_by(UUID deleted_by) { this.deleted_by = deleted_by; }

    public LocalDateTime getDeleted_at() { return deleted_at; }
    public void setDeleted_at(LocalDateTime deleted_at) { this.deleted_at = deleted_at; }
}
