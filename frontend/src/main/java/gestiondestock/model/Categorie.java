package gestiondestock.model;

import javafx.beans.property.*;

public class Categorie {
    private final StringProperty id;
    private final StringProperty nom;
    private final StringProperty description;

    public Categorie() {
        this.id = new SimpleStringProperty();
        this.nom = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
    }

    public Categorie(String nom, String description) {
        this.id = new SimpleStringProperty();
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
    }

    // Properties
    public StringProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty descriptionProperty() { return description; }

    // Getters and Setters
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
}