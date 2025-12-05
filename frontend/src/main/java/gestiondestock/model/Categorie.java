package gestiondestock.model;

import java.util.UUID;

public class Categorie {

    private UUID id;
    private String nom;
    private String description;

    public Categorie() {
    }

    public Categorie(UUID id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
