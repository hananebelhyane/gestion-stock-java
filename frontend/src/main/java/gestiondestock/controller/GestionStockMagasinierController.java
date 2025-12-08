package gestiondestock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gestiondestock.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class GestionStockMagasinierController {

    private static final String BASE_URL = "http://localhost:8080/api/stock";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ========== ONGLET ALERTES ==========
    @FXML private TableView<StockAlert> tableAlertes;
    @FXML private TableColumn<StockAlert, String> colAlerteProduit;
    @FXML private TableColumn<StockAlert, Integer> colAlerteQte;
    @FXML private TableColumn<StockAlert, Integer> colAlerteSeuil;
    @FXML private TableColumn<StockAlert, String> colAlerteNiveau;
    @FXML private TableColumn<StockAlert, Void> colAlerteActions;

    // ========== ONGLET MOUVEMENTS DU JOUR ==========
    @FXML private TableView<MouvementStock> tableMouvements;
    @FXML private TableColumn<MouvementStock, String> colMouvType;
    @FXML private TableColumn<MouvementStock, String> colMouvProduit;
    @FXML private TableColumn<MouvementStock, Integer> colMouvQte;
    @FXML private TableColumn<MouvementStock, String> colMouvDate;
    @FXML private TableColumn<MouvementStock, String> colMouvMagasinier;
    @FXML private TableColumn<MouvementStock, String> colMouvRef;

    // ========== ONGLET HISTORIQUE ==========
    @FXML private TableView<HistoriqueStock> tableHistorique;
    @FXML private TableColumn<HistoriqueStock, String> colHistType;
    @FXML private TableColumn<HistoriqueStock, String> colHistProduit;
    @FXML private TableColumn<HistoriqueStock, Integer> colHistQte;
    @FXML private TableColumn<HistoriqueStock, String> colHistDate;
    @FXML private TableColumn<HistoriqueStock, String> colHistMagasinier;
    
    @FXML private ComboBox<String> cbFiltreProduit;
    @FXML private ComboBox<String> cbFiltreType;
    @FXML private Button btnFiltrer;
    @FXML private Button btnResetFiltre;

    // ========== BADGES STATISTIQUES ==========
    @FXML private Label lblTotalEntrees;
    @FXML private Label lblTotalSorties;
    @FXML private Label lblProduitsAlerte;
    @FXML private Label lblMouvementsJour;

    // ========== BOUTONS ACTIONS ==========
    @FXML private Button btnNouvelleEntree;
    @FXML private Button btnNouvelleSortie;
    @FXML private Button btnRafraichir;

    private ObservableList<StockAlert> dataAlertes = FXCollections.observableArrayList();
    private ObservableList<MouvementStock> dataMouvements = FXCollections.observableArrayList();
    private ObservableList<HistoriqueStock> dataHistorique = FXCollections.observableArrayList();
    private ObservableList<HistoriqueStock> allHistorique = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableAlertes();
        setupTableMouvements();
        setupTableHistorique();
        setupFiltres();
        chargerToutesDonnees();
    }

    // ==================== CONFIGURATION TABLES ====================

    private void setupTableAlertes() {
        colAlerteProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        
        // Colonne Quantité avec style CSS
        colAlerteQte.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
        colAlerteQte.setCellFactory(col -> new TableCell<StockAlert, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("qte-rupture", "qte-critique", "qte-faible");
                } else {
                    setText(item.toString());
                    StockAlert alert = getTableView().getItems().get(getIndex());
                    
                    // Retirer les anciennes classes
                    getStyleClass().removeAll("qte-rupture", "qte-critique", "qte-faible");
                    
                    // Ajouter la classe appropriée
                    if (alert.getNiveauAlerte().equals("RUPTURE")) {
                        getStyleClass().add("qte-rupture");
                    } else if (alert.getNiveauAlerte().equals("CRITIQUE")) {
                        getStyleClass().add("qte-critique");
                    } else {
                        getStyleClass().add("qte-faible");
                    }
                }
            }
        });

        colAlerteSeuil.setCellValueFactory(new PropertyValueFactory<>("seuilAlerte"));
        
        // Colonne Niveau avec Badge et classes CSS
        colAlerteNiveau.setCellValueFactory(new PropertyValueFactory<>("niveauAlerte"));
        colAlerteNiveau.setCellFactory(col -> new TableCell<StockAlert, String>() {
            private final Label badge = new Label();
            
            {
                badge.getStyleClass().add("niveau-badge");
                setGraphic(badge);
                setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    badge.setText("");
                    badge.getStyleClass().removeAll("niveau-critique", "niveau-faible", "niveau-normal");
                } else {
                    badge.setText(item);
                    
                    // Retirer les anciennes classes
                    badge.getStyleClass().removeAll("niveau-critique", "niveau-faible", "niveau-normal");
                    
                    // Ajouter la classe appropriée
                    switch (item) {
                        case "RUPTURE":
                        case "CRITIQUE":
                            badge.getStyleClass().add("niveau-critique");
                            break;
                        case "FAIBLE":
                            badge.getStyleClass().add("niveau-faible");
                            break;
                        default:
                            badge.getStyleClass().add("niveau-normal");
                            break;
                    }
                }
            }
        });

        // Colonne Actions avec bouton + Entrée
        colAlerteActions.setCellFactory(column -> new TableCell<StockAlert, Void>() {
            private final Button btnEntree = new Button("+ Entrée");
            {
                btnEntree.getStyleClass().add("btn-action-entree");
                btnEntree.setOnAction(e -> {
                    StockAlert alert = getTableView().getItems().get(getIndex());
                    ouvrirDialogueEntree(alert.getProduitId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEntree);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void setupTableMouvements() {
        // Colonne Type avec badge coloré
        colMouvType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colMouvType.setCellFactory(col -> new TableCell<MouvementStock, String>() {
            private final Label badge = new Label();
            
            {
                badge.getStyleClass().add("niveau-badge");
                setGraphic(badge);
                setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    badge.setText("");
                    badge.getStyleClass().removeAll("niveau-normal", "niveau-critique");
                } else {
                    badge.setText(item);
                    badge.getStyleClass().removeAll("niveau-normal", "niveau-critique");
                    
                    if (item.equals("ENTREE")) {
                        badge.getStyleClass().add("niveau-normal");
                    } else {
                        badge.getStyleClass().add("niveau-critique");
                    }
                }
            }
        });

        colMouvProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        colMouvQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        
        colMouvDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDateMouvement();
            String formatted = date != null ? date.format(dateFormatter) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        colMouvMagasinier.setCellValueFactory(new PropertyValueFactory<>("magasinierNom"));
        colMouvRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
    }

    private void setupTableHistorique() {
        // Colonne Type avec badge coloré
        colHistType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colHistType.setCellFactory(col -> new TableCell<HistoriqueStock, String>() {
            private final Label badge = new Label();
            
            {
                badge.getStyleClass().add("niveau-badge");
                setGraphic(badge);
                setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    badge.setText("");
                    badge.getStyleClass().removeAll("niveau-normal", "niveau-critique");
                } else {
                    badge.setText(item);
                    badge.getStyleClass().removeAll("niveau-normal", "niveau-critique");
                    
                    if (item.equals("ENTREE")) {
                        badge.getStyleClass().add("niveau-normal");
                    } else {
                        badge.getStyleClass().add("niveau-critique");
                    }
                }
            }
        });

        colHistProduit.setCellValueFactory(new PropertyValueFactory<>("produitNom"));
        colHistQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        
        colHistDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDate();
            String formatted = date != null ? date.format(dateFormatter) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        colHistMagasinier.setCellValueFactory(new PropertyValueFactory<>("magasinierNom"));
    }

    private void setupFiltres() {
        cbFiltreType.setItems(FXCollections.observableArrayList("TOUS", "ENTREE", "SORTIE"));
        cbFiltreType.setValue("TOUS");
    }

    // ==================== CHARGEMENT DONNÉES ====================

    private void chargerToutesDonnees() {
        chargerAlertes();
        chargerMouvementsDuJour();
        chargerListeProduits();
        calculerStatistiques();
    }

    private void chargerAlertes() {
        try {
            List<StockAlert> alertes = apiGetAlertes();
            dataAlertes.setAll(alertes);
            tableAlertes.setItems(dataAlertes);
        } catch (Exception e) {
            showError("Erreur chargement alertes : " + e.getMessage());
        }
    }

    private void chargerMouvementsDuJour() {
        try {
            List<MouvementStock> mouvements = apiGetMouvementsDuJour();
            dataMouvements.setAll(mouvements);
            tableMouvements.setItems(dataMouvements);
        } catch (Exception e) {
            showError("Erreur chargement mouvements : " + e.getMessage());
        }
    }

    private void chargerListeProduits() {
        try {
            List<Stock> stocks = apiGetAllStock();
            ObservableList<String> produits = FXCollections.observableArrayList("TOUS");
            for (Stock s : stocks) {
                if (s.getProduit() != null && s.getProduit().getNom() != null) {
                    produits.add(s.getProduit().getNom());
                }
            }
            cbFiltreProduit.setItems(produits);
            cbFiltreProduit.setValue("TOUS");
        } catch (Exception e) {
            System.err.println("Erreur chargement produits : " + e.getMessage());
        }
    }

    private void calculerStatistiques() {
        try {
            // Récupérer tous les mouvements du jour
            List<MouvementStock> mouvements = apiGetMouvementsDuJour();
            
            int totalEntrees = 0;
            int totalSorties = 0;
            
            for (MouvementStock m : mouvements) {
                if (m.getType().equals("ENTREE")) {
                    totalEntrees += m.getQuantite();
                } else {
                    totalSorties += m.getQuantite();
                }
            }
            
            lblTotalEntrees.setText(String.valueOf(totalEntrees));
            lblTotalSorties.setText(String.valueOf(totalSorties));
            lblProduitsAlerte.setText(String.valueOf(dataAlertes.size()));
            lblMouvementsJour.setText(String.valueOf(mouvements.size()));
            
        } catch (Exception e) {
            System.err.println("Erreur calcul stats : " + e.getMessage());
        }
    }

    // ==================== ACTIONS BOUTONS ====================

    @FXML
    private void handleNouvelleEntree() {
        ouvrirDialogueEntree(null);
    }

    @FXML
    private void handleNouvelleSortie() {
        ouvrirDialogueSortie(null);
    }

    @FXML
    private void handleRafraichir() {
        chargerToutesDonnees();
        showInfo("Données rafraîchies avec succès");
    }

    @FXML
    private void handleFiltrer() {
        String produitFiltre = cbFiltreProduit.getValue();
        String typeFiltre = cbFiltreType.getValue();

        ObservableList<HistoriqueStock> filtres = FXCollections.observableArrayList();
        
        for (HistoriqueStock h : allHistorique) {
            boolean produitMatch = produitFiltre.equals("TOUS") || h.getProduitNom().equals(produitFiltre);
            boolean typeMatch = typeFiltre.equals("TOUS") || h.getType().equals(typeFiltre);
            
            if (produitMatch && typeMatch) {
                filtres.add(h);
            }
        }
        
        dataHistorique.setAll(filtres);
        tableHistorique.setItems(dataHistorique);
        showInfo("Filtres appliqués : " + filtres.size() + " résultat(s)");
    }

    @FXML
    private void handleResetFiltre() {
        cbFiltreProduit.setValue("TOUS");
        cbFiltreType.setValue("TOUS");
        tableHistorique.setItems(allHistorique);
        showInfo("Filtres réinitialisés");
    }

    @FXML
    private void chargerHistoriqueProduit() {
        String produitNom = cbFiltreProduit.getValue();
        if (produitNom == null || produitNom.equals("TOUS")) {
            showWarning("Veuillez sélectionner un produit spécifique");
            return;
        }

        try {
            // Trouver l'ID du produit
            List<Stock> stocks = apiGetAllStock();
            UUID produitId = null;
            for (Stock s : stocks) {
                if (s.getProduit() != null && s.getProduit().getNom().equals(produitNom)) {
                    produitId = s.getProduit().getId();
                    break;
                }
            }

            if (produitId == null) {
                showError("Produit introuvable");
                return;
            }

            // Charger entrées et sorties
            List<EntreeStock> entrees = apiGetHistoriqueEntrees(produitId);
            List<SortieStock> sorties = apiGetHistoriqueSorties(produitId);

            ObservableList<HistoriqueStock> historique = FXCollections.observableArrayList();
            
            for (EntreeStock e : entrees) {
                historique.add(new HistoriqueStock(
                    "ENTREE", e.getProduitNom(), e.getQuantite(), 
                    e.getDateEntree(), e.getMagasinierNom()
                ));
            }
            
            for (SortieStock s : sorties) {
                historique.add(new HistoriqueStock(
                    "SORTIE", s.getProduitNom(), s.getQuantite(), 
                    s.getDateSortie(), s.getMagasinierNom()
                ));
            }

            // Trier par date décroissante
            historique.sort((h1, h2) -> h2.getDate().compareTo(h1.getDate()));

            allHistorique.setAll(historique);
            dataHistorique.setAll(historique);
            tableHistorique.setItems(dataHistorique);
            
            showInfo("Historique chargé : " + historique.size() + " mouvement(s)");

        } catch (Exception e) {
            showError("Erreur chargement historique : " + e.getMessage());
        }
    }

    // ==================== DIALOGUES ====================

    private void ouvrirDialogueEntree(UUID produitId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EntreeStockDialog.fxml"));
            Parent root = loader.load();
            EntreeStockDialogController controller = loader.getController();
            
            if (produitId != null) {
                controller.setSelectedProduitId(produitId);
            }
            
            controller.setOnSuccess(v -> {
                chargerToutesDonnees();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle Entrée de Stock");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnNouvelleEntree.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            showError("Impossible d'ouvrir le dialogue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ouvrirDialogueSortie(UUID produitId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SortieStockDialog.fxml"));
            Parent root = loader.load();
            SortieStockDialogController controller = loader.getController();
            
            if (produitId != null) {
                controller.setSelectedProduitId(produitId);
            }
            
            controller.setOnSuccess(v -> {
                chargerToutesDonnees();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle Sortie de Stock");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnNouvelleSortie.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            showError("Impossible d'ouvrir le dialogue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== API CALLS ====================

    private List<StockAlert> apiGetAlertes() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/alertes"))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), new TypeReference<List<StockAlert>>() {});
    }

    private List<MouvementStock> apiGetMouvementsDuJour() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mouvements/aujourd-hui"))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), new TypeReference<List<MouvementStock>>() {});
    }

    private List<EntreeStock> apiGetHistoriqueEntrees(UUID produitId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/produit/" + produitId + "/entrees"))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), new TypeReference<List<EntreeStock>>() {});
    }

    private List<SortieStock> apiGetHistoriqueSorties(UUID produitId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/produit/" + produitId + "/sorties"))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), new TypeReference<List<SortieStock>>() {});
    }

    private List<Stock> apiGetAllStock() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), new TypeReference<List<Stock>>() {});
    }

    private String getAuthToken() {
        return Session.get().getToken();
    }

    // ==================== UTILITAIRES ====================

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}