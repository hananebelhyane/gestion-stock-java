package gestiondestock.model;

/**
 * Simple POJO used for JSON payloads to avoid JavaFX property reflection issues.
 */
public class CategorieDTO {
    private String id;
    private String nom;
    private String description;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
