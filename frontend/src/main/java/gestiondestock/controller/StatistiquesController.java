package gestiondestock.controller;

import gestiondestock.dto.ClientTopDTO;
import gestiondestock.dto.ProduitVenduDTO;
import gestiondestock.dto.StatistiquesGeneralesDTO;
import gestiondestock.dto.EvolutionVentesDTO;
import gestiondestock.dto.VenteParPeriodeDTO;
import gestiondestock.service.PdfExportService;
import gestiondestock.service.StatistiquesServiceClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable {

    @FXML
    private ComboBox<String> periodComboBox;

    @FXML
    private Label totalProduitsLabel, totalVentesLabel, totalCommandesLabel, totalClientsLabel;

    @FXML
    private LineChart<String, Number> ventesLineChart;

    @FXML
    private BarChart<String, Number> produitsBarChart;

    @FXML
    private PieChart categoriesPieChart;

    private final StatistiquesServiceClient serviceClient = new StatistiquesServiceClient();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPeriodComboBox();
        loadStatistiques();
    }

    private void setupPeriodComboBox() {
        ObservableList<String> periodes = FXCollections.observableArrayList(
                "7 derniers jours",
                "30 derniers jours",
                "12 derniers mois",
                "3 dernières années"
        );
        periodComboBox.setItems(periodes);
        periodComboBox.getSelectionModel().selectFirst();

        periodComboBox.setOnAction(event -> loadEvolutionVentes());
    }

    private void loadStatistiques() {
        loadDashboard();
        loadEvolutionVentes();
    }

    private void loadDashboard() {
        // Statistiques générales
        StatistiquesGeneralesDTO stats = serviceClient.getStatistiquesGenerales();
        if (stats != null) {
            Platform.runLater(() -> {
                totalProduitsLabel.setText(String.valueOf(stats.getNombreTotalProduits()));
                totalVentesLabel.setText(decimalFormat.format(stats.getChiffreAffairesTotal()) + " DH");
                totalCommandesLabel.setText(String.valueOf(stats.getNombreTotalCommandes()));
                totalClientsLabel.setText(String.valueOf(stats.getNombreTotalClients()));

                // Graphiques
                List<ProduitVenduDTO> topProduits = serviceClient.getProduitsLesPlusVendus();
                System.out.println("Nombre de produits : " + topProduits.size());
                loadTopProduitsChart(topProduits);

                List<ClientTopDTO> topClients = serviceClient.getTopClients();
                System.out.println(" Nombre de clients : " + topClients.size());
                loadTopClientsChart(topClients);
            });
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les statistiques générales.");
        }
    }


    private void loadTopProduitsChart(List<ProduitVenduDTO> topProduits) {
        if (topProduits == null || topProduits.isEmpty()) {
            System.out.println(" Aucun produit trouvé !");
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantité vendue");

        int size = Math.min(topProduits.size(), 5);
        for (int i = 0; i < size; i++) {
            ProduitVenduDTO produit = topProduits.get(i);
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    produit.getNomProduit(),
                    produit.getQuantiteVendue()
            );
            series.getData().add(data);
        }

        produitsBarChart.getData().clear();
        produitsBarChart.getData().add(series);


    }


    private void loadTopClientsChart(List<ClientTopDTO> topClients) {
        if (topClients == null || topClients.isEmpty()) {
            System.out.println(" Aucun client trouvé !");
            categoriesPieChart.setTitle("Aucune donnée disponible");
            return;
        }

        System.out.println(" Chargement de " + topClients.size() + " clients");

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        double total = topClients.stream().mapToDouble(ClientTopDTO::getTotalAchats).sum();

        System.out.println(" Total achats : " + total);

        if (total == 0) {
            System.out.println(" Total des achats = 0 !");
            categoriesPieChart.setTitle("Aucun achat enregistré");
            return;
        }

        for (ClientTopDTO client : topClients) {
            double percentage = (client.getTotalAchats() / total) * 100;
            String label = String.format("%s %s (%.1f%%)",
                    client.getPrenom() != null ? client.getPrenom() : "",
                    client.getNom() != null ? client.getNom() : "",
                    percentage);

            System.out.println(" Client : " + label + " - " + client.getTotalAchats() + " DH");

            PieChart.Data pieData = new PieChart.Data(label, client.getTotalAchats());
            pieChartData.add(pieData);
        }

        categoriesPieChart.setData(pieChartData);
        categoriesPieChart.setStartAngle(90);
        categoriesPieChart.setTitle("");


    }


    private void loadEvolutionVentes() {
        EvolutionVentesDTO evolution = serviceClient.getEvolutionVentes();
        if (evolution == null) {
            System.out.println(" Impossible de charger l'évolution des ventes");
            return;
        }

        String selected = periodComboBox.getSelectionModel().getSelectedItem();
        List<VenteParPeriodeDTO> ventesData;

        switch (selected) {
            case "7 derniers jours":
            case "30 derniers jours":
                ventesData = evolution.getVentesParJour();
                break;
            case "12 derniers mois":
                ventesData = evolution.getVentesParMois();
                break;
            case "3 dernières années":
                ventesData = evolution.getVentesParAnnee();
                break;
            default:
                ventesData = evolution.getVentesParJour();
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chiffre d'affaires");

        for (VenteParPeriodeDTO vente : ventesData) {
            series.getData().add(new XYChart.Data<>(vente.getPeriode(), vente.getMontant()));
        }

        ventesLineChart.getData().clear();
        ventesLineChart.getData().add(series);


    }


    @FXML
    private void handleRefresh() {
        loadStatistiques();
        showAlert(Alert.AlertType.INFORMATION, "Actualisation", "Les statistiques ont été actualisées avec succès.");
    }



    @FXML
    private void handleExport() {
        try {
            System.out.println("Début de l'export PDF...");

            // Récupérer les données
            StatistiquesGeneralesDTO stats = serviceClient.getStatistiquesGenerales();
            List<ProduitVenduDTO> topProduits = serviceClient.getProduitsLesPlusVendus();
            List<ClientTopDTO> topClients = serviceClient.getTopClients();
            String periode = periodComboBox.getSelectionModel().getSelectedItem();

            System.out.println("Données récupérées");


            File pdfFile = PdfExportService.exportStatistiquesPdf(stats, topProduits, topClients, periode);

            if (pdfFile != null && pdfFile.exists()) {
                System.out.println("PDF créé : " + pdfFile.getAbsolutePath());

                showAlert(Alert.AlertType.INFORMATION,
                        "Export réussi",
                        "Le rapport a été exporté avec succès !\n\nEmplacement : " + pdfFile.getAbsolutePath());


                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            } else {
                System.out.println(" Erreur : PDF null ou inexistant");
                showAlert(Alert.AlertType.ERROR,
                        "Erreur",
                        "Impossible de générer le rapport PDF.");
            }
        } catch (Exception e) {
            System.out.println(" Exception : " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Une erreur s'est produite lors de l'export : " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}