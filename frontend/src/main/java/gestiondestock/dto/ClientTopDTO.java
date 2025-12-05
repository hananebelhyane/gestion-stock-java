package gestiondestock.dto;

public class ClientTopDTO {

    private String clientId;
    private String nom;
    private String prenom;
    private double totalAchats;
    private int nombreCommandes;

    // --- Getters & Setters ---
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

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

    public double getTotalAchats() {
        return totalAchats;
    }

    public void setTotalAchats(double totalAchats) {
        this.totalAchats = totalAchats;
    }

    public int getNombreCommandes() {
        return nombreCommandes;
    }

    public void setNombreCommandes(int nombreCommandes) {
        this.nombreCommandes = nombreCommandes;
    }

    // Optionnel : pour JavaFX TableView binding
    // private final StringProperty nomComplet = new SimpleStringProperty();
}
