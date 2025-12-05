package gestiondestock.dto;

public class StatistiquesCommandesDTO {

    private int totalCommandes;
    private int commandesEnAttente;
    private int commandesConfirmees;
    private int commandesAnnulees;
    private double tauxConfirmation;
    private double tauxAnnulation;

    // --- Getters & Setters ---
    public int getTotalCommandes() {
        return totalCommandes;
    }

    public void setTotalCommandes(int totalCommandes) {
        this.totalCommandes = totalCommandes;
    }

    public int getCommandesEnAttente() {
        return commandesEnAttente;
    }

    public void setCommandesEnAttente(int commandesEnAttente) {
        this.commandesEnAttente = commandesEnAttente;
    }

    public int getCommandesConfirmees() {
        return commandesConfirmees;
    }

    public void setCommandesConfirmees(int commandesConfirmees) {
        this.commandesConfirmees = commandesConfirmees;
    }

    public int getCommandesAnnulees() {
        return commandesAnnulees;
    }

    public void setCommandesAnnulees(int commandesAnnulees) {
        this.commandesAnnulees = commandesAnnulees;
    }

    public double getTauxConfirmation() {
        return tauxConfirmation;
    }

    public void setTauxConfirmation(double tauxConfirmation) {
        this.tauxConfirmation = tauxConfirmation;
    }

    public double getTauxAnnulation() {
        return tauxAnnulation;
    }

    public void setTauxAnnulation(double tauxAnnulation) {
        this.tauxAnnulation = tauxAnnulation;
    }
}
