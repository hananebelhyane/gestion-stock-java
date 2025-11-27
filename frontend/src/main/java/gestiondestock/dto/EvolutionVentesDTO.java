package gestiondestock.dto;

import java.util.List;

public class EvolutionVentesDTO {

    private List<VenteParPeriodeDTO> ventesParJour;
    private List<VenteParPeriodeDTO> ventesParMois;
    private List<VenteParPeriodeDTO> ventesParAnnee;

    // --- Getters & Setters ---
    public List<VenteParPeriodeDTO> getVentesParJour() {
        return ventesParJour;
    }

    public void setVentesParJour(List<VenteParPeriodeDTO> ventesParJour) {
        this.ventesParJour = ventesParJour;
    }

    public List<VenteParPeriodeDTO> getVentesParMois() {
        return ventesParMois;
    }

    public void setVentesParMois(List<VenteParPeriodeDTO> ventesParMois) {
        this.ventesParMois = ventesParMois;
    }

    public List<VenteParPeriodeDTO> getVentesParAnnee() {
        return ventesParAnnee;
    }

    public void setVentesParAnnee(List<VenteParPeriodeDTO> ventesParAnnee) {
        this.ventesParAnnee = ventesParAnnee;
    }
}
