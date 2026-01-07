package gestiondestock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandeDTO {
    private String id;
    private String statut;
    
    // Fields for Client Command
    private ClientSummary client;
    private String dateCommande; // String or LocalDateTime depending on backend
    private Integer seuilMax;

    // Fields for Supplier Command
    private ProduitSummary produit;
    private String commandeDate; // Supplier uses this name

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public ClientSummary getClient() { return client; }
    public void setClient(ClientSummary client) { this.client = client; }

    public String getDateCommande() { return dateCommande; }
    public void setDateCommande(String dateCommande) { this.dateCommande = dateCommande; }

    public Integer getSeuilMax() { return seuilMax; }
    public void setSeuilMax(Integer seuilMax) { this.seuilMax = seuilMax; }

    public ProduitSummary getProduit() { return produit; }
    public void setProduit(ProduitSummary produit) { this.produit = produit; }

    public String getCommandeDate() { return commandeDate; }
    public void setCommandeDate(String commandeDate) { this.commandeDate = commandeDate; }

    // Helper to get a unified date
    public String getUnifiedDate() {
        if (dateCommande != null) return dateCommande;
        if (commandeDate != null) return commandeDate;
        return "";
    }

    // Helper to get a unified name (Client Name or Product Name)
    public String getUnifiedName() {
        if (client != null) return client.getNom() + " " + client.getPrenom();
        if (produit != null) return produit.getNom();
        return "Inconnu";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientSummary {
        private String id;
        private String nom;
        private String prenom;
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProduitSummary {
        private String id;
        private String nom;
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
    }
}
