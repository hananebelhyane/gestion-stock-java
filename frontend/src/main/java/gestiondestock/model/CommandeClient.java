package gestiondestock.model;

public class CommandeClient {

    private String id;
    private Client client;  // ✅ Objet Client complet
    private String dateCommande;  // ✅ String pour éviter problème Gson
    private String statut;
    private Integer seuilMax;

    // Classe interne pour Client
    public static class Client {

        private String id;
        private String nom;
        private String prenom;
        private String username;
        private String telephone;
        private String adresse;

        // Getters et Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }
    }

    // Constructeurs
    public CommandeClient() {
    }

    public CommandeClient(String id, Client client, String dateCommande,
            String statut, Integer seuilMax) {
        this.id = id;
        this.client = client;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.seuilMax = seuilMax;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // ✅ Méthode helper pour afficher le nom complet du client
    public String getClientNom() {
        return client != null ? client.getPrenom() + " " + client.getNom() : "";
    }

    public String getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getSeuilMax() {
        return seuilMax;
    }

    public void setSeuilMax(Integer seuilMax) {
        this.seuilMax = seuilMax;
    }
}
