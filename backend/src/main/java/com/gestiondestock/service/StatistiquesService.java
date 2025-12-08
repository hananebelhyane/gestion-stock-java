package com.gestiondestock.service;

import com.gestiondestock.dto.*;
import com.gestiondestock.entity.AlerteStock;
import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.entity.Stock;
import com.gestiondestock.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.lang.Double;

@Service
public class StatistiquesService {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CommandeClientRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AlerteStockRepository alerteStockRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private CommandeClientRepository commandeClientRepository;

    //  STATISTIQUES GÉNÉRALES

    public StatistiquesGeneralesDTO getStatistiquesGenerales() {
        StatistiquesGeneralesDTO stats = new StatistiquesGeneralesDTO();

        stats.setNombreTotalProduits(produitRepository.count());
        stats.setNombreTotalClients(clientRepository.countClientsActifs());
        stats.setNombreTotalCommandes(commandeClientRepository.count());


        Double valeurStock = produitRepository.calculerValeurTotaleStock();
        stats.setValeurTotaleStock(valeurStock != null ? valeurStock : 0.0);

        Double caTotal = ligneCommandeRepository.calculerChiffreAffairesTotal();
        stats.setChiffreAffairesTotal(caTotal != null ? caTotal : 0.0);


        stats.setNombreProduitsRupture(stockRepository.countProduitsEnRupture());
        stats.setNombreAlertesNonLues(alerteStockRepository.countByStatut(AlerteStock.StatutAlerte.NON_LU));

        return stats;
    }

    // STATISTIQUES DES VENTES

    public StatistiquesVentesDTO getStatistiquesVentes() {
        StatistiquesVentesDTO stats = new StatistiquesVentesDTO();

        Double caTotal = ligneCommandeRepository.calculerChiffreAffairesTotal();
        stats.setChiffreAffairesTotal(caTotal != null ? caTotal : 0.0);

        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMois = LocalDateTime.now();
        Double caMois = ligneCommandeRepository.calculerChiffreAffairesTotalParPeriode(debutMois, finMois);
        stats.setChiffreAffairesMois(caMois != null ? caMois : 0.0);

        LocalDateTime debutAnnee = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        Double caAnnee = ligneCommandeRepository.calculerChiffreAffairesTotalParPeriode(debutAnnee, finMois);
        stats.setChiffreAffairesAnnee(caAnnee != null ? caAnnee : 0.0);

        stats.setNombreCommandesMois((long) commandeClientRepository.findByDateCommandeDuMois().size());


        stats.setNombreCommandesEnAttente(
                commandeClientRepository.countByStatut(CommandeClient.StatutCommande.en_attente)
        );
        stats.setNombreCommandesConfirmees(
                commandeClientRepository.countByStatut(CommandeClient.StatutCommande.confirmee)
        );
        stats.setNombreCommandesAnnulees(
                commandeClientRepository.countByStatut(CommandeClient.StatutCommande.annulee)
        );

        stats.setTopProduitsVendus(getTopProduitsVendus(5));
        stats.setTopClients(getTopClients(5));

        return stats;
    }

    //  TOP PRODUITS VENDUS

    public List<ProduitVenduDTO> getTopProduitsVendus(int limit) {
        List<Object[]> results = ligneCommandeRepository.findTopProduitsVendus();
        List<ProduitVenduDTO> topProduits = new ArrayList<>();

        int count = 0;
        for (Object[] result : results) {

            if (count >= limit) break;

            ProduitVenduDTO dto = new ProduitVenduDTO();
            dto.setProduitId((UUID) result[0]);
            dto.setNomProduit((String) result[1]);

            if (result[2] instanceof Long) {
                dto.setQuantiteVendue((Long) result[2]);
            } else if (result[2] instanceof Integer) {
                dto.setQuantiteVendue(((Integer) result[2]).longValue());
            } else if (result[2] instanceof BigDecimal) {
                dto.setQuantiteVendue(((BigDecimal) result[2]).longValue());
            }

            try {
                Produit produit = produitRepository.findById((UUID) result[0]).orElse(null);
                if (produit != null) {
                    dto.setUrlImage(produit.getUrlImage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            topProduits.add(dto);
            count++;
        }

        return topProduits;
    }

    // TOP CLIENTS

    public List<ClientTopDTO> getTopClients(int limit) {
        List<Object[]> results = clientRepository.findTopClients();
        List<ClientTopDTO> topClients = new ArrayList<>();

        int count = 0;
        for (Object[] result : results) {

            if (count >= limit) break;

            ClientTopDTO dto = new ClientTopDTO();
            dto.setClientId((UUID) result[0]);
            dto.setNom((String) result[1]);
            dto.setPrenom((String) result[2]);
            dto.setTotalAchats((Double) result[3]);

            List<CommandeClient> commandes = commandeClientRepository
                    .findByStatut(CommandeClient.StatutCommande.confirmee)
                    .stream()
                    .filter(c -> c.getClient().getId().equals(dto.getClientId()))
                    .collect(Collectors.toList());

            dto.setNombreCommandes((long) commandes.size());
            topClients.add(dto);
            count++;
        }

        return topClients;
    }

    //STATISTIQUES DE STOCK

    public StatistiquesStockDTO getStatistiquesStock() {
        StatistiquesStockDTO stats = new StatistiquesStockDTO();

        stats.setNombreProduitsTotal(produitRepository.count());

        List<Stock> stocksRupture = stockRepository.findProduitsEnRupture();
        stats.setNombreProduitsRupture((long) stocksRupture.size());
        stats.setProduitsRupture(convertStockToDTO(stocksRupture));

        List<Stock> stocksFaibles = stockRepository.findProduitsStockFaible();
        stats.setNombreProduitsStockFaible((long) stocksFaibles.size());
        stats.setProduitsStockFaible(convertStockToDTO(stocksFaibles));

        Double valeurStock = produitRepository.calculerValeurTotaleStock();
        stats.setValeurTotaleStock(valeurStock != null ? valeurStock : 0.0);

        return stats;
    }

    //  CONVERTIR STOCK EN DTO

    private List<ProduitStockDTO> convertStockToDTO(List<Stock> stocks) {
        List<ProduitStockDTO> dtos = new ArrayList<>();

        for (Stock stock : stocks) {
            ProduitStockDTO dto = new ProduitStockDTO();
            dto.setProduitId(stock.getProduit().getId());
            dto.setNomProduit(stock.getProduit().getNom());
            dto.setQuantiteDisponible(stock.getQuantiteDisponible());
            dto.setSeuilAlerte(stock.getSeuilAlerte());
            dto.setPrixUnitaire(stock.getProduit().getPrixUnitaire());
            dto.setUrlImage(stock.getProduit().getUrlImage());

            if (stock.getProduit().getCategorie() != null) {
                dto.setCategorie(stock.getProduit().getCategorie().getNom());
            }

            dtos.add(dto);
        }

        return dtos;
    }

    // STATISTIQUES DES COMMANDES

    public StatistiquesCommandesDTO getStatistiquesCommandes() {
        StatistiquesCommandesDTO stats = new StatistiquesCommandesDTO();

        long total = commandeClientRepository.count();
        stats.setTotalCommandes(total);

        long enAttente = commandeClientRepository.countByStatut(CommandeClient.StatutCommande.en_attente);
        stats.setCommandesEnAttente(enAttente);

        long confirmees = commandeClientRepository.countByStatut(CommandeClient.StatutCommande.confirmee);
        stats.setCommandesConfirmees(confirmees);

        long annulees = commandeClientRepository.countByStatut(CommandeClient.StatutCommande.annulee);
        stats.setCommandesAnnulees(annulees);

        if (total > 0) {
            stats.setTauxConfirmation((confirmees * 100.0) / total);
            stats.setTauxAnnulation((annulees * 100.0) / total);
        } else {
            stats.setTauxConfirmation(0.0);
            stats.setTauxAnnulation(0.0);
        }

        return stats;
    }

    // ÉVOLUTION DES VENTES

    public EvolutionVentesDTO getEvolutionVentes() {
        EvolutionVentesDTO evolution = new EvolutionVentesDTO();

        evolution.setVentesParJour(getVentesParJours(7));
        evolution.setVentesParMois(getVentesParMois(12));
        evolution.setVentesParAnnee(getVentesParAnnees(3));

        return evolution;
    }

    //  VENTES PAR JOURS

    private List<VenteParPeriodeDTO> getVentesParJours(int nombreJours) {
        List<VenteParPeriodeDTO> ventes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = nombreJours - 1; i >= 0; i--) {
            LocalDateTime debut = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fin = debut.withHour(23).withMinute(59).withSecond(59);

            List<CommandeClient> commandes = commandeClientRepository.findCommandesParPeriode(debut, fin);
            Double montant = ligneCommandeRepository.calculerChiffreAffairesTotalParPeriode(debut, fin);

            VenteParPeriodeDTO vente = new VenteParPeriodeDTO();
            vente.setPeriode(debut.format(formatter));
            vente.setMontant(montant != null ? montant : 0.0);
            vente.setNombreCommandes((long) commandes.size());

            ventes.add(vente);
        }

        return ventes;
    }

    // VENTES PAR MOIS

    private List<VenteParPeriodeDTO> getVentesParMois(int nombreMois) {
        List<VenteParPeriodeDTO> ventes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = nombreMois - 1; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDateTime debut = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime fin = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            List<CommandeClient> commandes = commandeClientRepository.findCommandesParPeriode(debut, fin);
            Double montant = ligneCommandeRepository.calculerChiffreAffairesTotalParPeriode(debut, fin);

            VenteParPeriodeDTO vente = new VenteParPeriodeDTO();
            vente.setPeriode(yearMonth.format(formatter));
            vente.setMontant(montant != null ? montant : 0.0);
            vente.setNombreCommandes((long) commandes.size());

            ventes.add(vente);
        }

        return ventes;
    }

    // VENTES PAR ANNÉES

    private List<VenteParPeriodeDTO> getVentesParAnnees(int nombreAnnees) {
        List<VenteParPeriodeDTO> ventes = new ArrayList<>();

        for (int i = nombreAnnees - 1; i >= 0; i--) {
            int annee = LocalDateTime.now().getYear() - i;
            LocalDateTime debut = LocalDateTime.of(annee, 1, 1, 0, 0, 0);
            LocalDateTime fin = LocalDateTime.of(annee, 12, 31, 23, 59, 59);

            List<CommandeClient> commandes = commandeClientRepository.findCommandesParPeriode(debut, fin);
            Double montant = ligneCommandeRepository.calculerChiffreAffairesTotalParPeriode(debut, fin);

            VenteParPeriodeDTO vente = new VenteParPeriodeDTO();
            vente.setPeriode(String.valueOf(annee));
            vente.setMontant(montant != null ? montant : 0.0);
            vente.setNombreCommandes((long) commandes.size());

            ventes.add(vente);
        }

        return ventes;
    }

    //DASHBOARD COMPLET

    public DashboardDTO getDashboardComplet() {
        DashboardDTO dashboard = new DashboardDTO();

        dashboard.setStatistiquesGenerales(getStatistiquesGenerales());
        dashboard.setStatistiquesVentes(getStatistiquesVentes());
        dashboard.setStatistiquesStock(getStatistiquesStock());
        dashboard.setStatistiquesCommandes(getStatistiquesCommandes());
        dashboard.setEvolutionVentes(getEvolutionVentes());

        return dashboard;
    }
}
