package gestiondestock.model;

import javafx.beans.property.*;

public class Fournisseur {
    private final StringProperty id;
    private final StringProperty nom;
    private final StringProperty email;
    private final StringProperty telephone;
    private final StringProperty adresse;

    public Fournisseur() {
        this.id = new SimpleStringProperty();
        this.nom = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.telephone = new SimpleStringProperty();
        this.adresse = new SimpleStringProperty();
    }

    public Fournisseur(String nom, String email, String telephone, String adresse) {
        this.id = new SimpleStringProperty();
        this.nom = new SimpleStringProperty(nom);
        this.email = new SimpleStringProperty(email);
        this.telephone = new SimpleStringProperty(telephone);
        this.adresse = new SimpleStringProperty(adresse);
    }

    // Properties
    public StringProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty emailProperty() { return email; }
    public StringProperty telephoneProperty() { return telephone; }
    public StringProperty adresseProperty() { return adresse; }

    // Getters and Setters
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getTelephone() { return telephone.get(); }
    public void setTelephone(String telephone) { this.telephone.set(telephone); }

    public String getAdresse() { return adresse.get(); }
    public void setAdresse(String adresse) { this.adresse.set(adresse); }
}
