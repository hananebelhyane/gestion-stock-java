package gestiondestock.model;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // ignore les champs du back que le front n'utilise pas
public class Categorie {

    private UUID id;
    private String nom;

    // Constructeurs
    public Categorie() {
    }

    public Categorie(UUID id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters et setters
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
}
