package gestiondestock.model;

public class MagasinierProfileModel {
    private String id;
    private String nom;
    private String prenom;
    private String username;
    private String telephone;

    public MagasinierProfileModel() {}

    public MagasinierProfileModel(String id, String nom, String prenom, String username, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.username = username;
        this.telephone = telephone;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    @Override
    public String toString() {
        return "MagasinierProfileModel{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", username='" + username + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}