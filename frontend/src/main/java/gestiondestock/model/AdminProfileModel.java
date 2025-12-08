package gestiondestock.model;

public class AdminProfileModel {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String username;
    private String telephone;

    public AdminProfileModel() {}

    public AdminProfileModel(Long id, String nom, String prenom, String email, String username, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.username = username;
        this.telephone = telephone;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    @Override
    public String toString() {
        return "AdminProfileModel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}