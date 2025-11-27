package gestiondestock.model;

import javafx.beans.property.*;

public class ClientTop {

    private StringProperty clientId;
    private StringProperty nomClient;
    private DoubleProperty montantAchat;

    public ClientTop() {
        this.clientId = new SimpleStringProperty();
        this.nomClient = new SimpleStringProperty();
        this.montantAchat = new SimpleDoubleProperty();
    }

    // Getters & Properties
    public String getClientId() { return clientId.get(); }
    public void setClientId(String clientId) { this.clientId.set(clientId); }
    public StringProperty clientIdProperty() { return clientId; }

    public String getNomClient() { return nomClient.get(); }
    public void setNomClient(String nomClient) { this.nomClient.set(nomClient); }
    public StringProperty nomClientProperty() { return nomClient; }

    public double getMontantAchat() { return montantAchat.get(); }
    public void setMontantAchat(double montantAchat) { this.montantAchat.set(montantAchat); }
    public DoubleProperty montantAchatProperty() { return montantAchat; }
}
