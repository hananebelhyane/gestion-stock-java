package gestiondestock.model;

public class Admin {

    private String nom;
    private String prenom;
    private String email;
    private String username;
    private String telephone;
    private String motDePasse;

    // Constructeur
    public Admin(String nom, String prenom, String email, String username, String telephone, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.username = username;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters (nécessaires pour Gson)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
