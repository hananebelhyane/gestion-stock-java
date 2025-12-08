package gestiondestock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gestiondestock.model.AlerteStock;
import gestiondestock.model.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AlertsController {

    private static final String BASE_URL = "http://localhost:8080/api/alertes";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TableView<AlerteStock> tableAlertes;
    @FXML private TableColumn<AlerteStock, String> colNiveau;
    @FXML private TableColumn<AlerteStock, String> colProduit;
    @FXML private TableColumn<AlerteStock, String> colMessage;
    @FXML private TableColumn<AlerteStock, String> colDate;
    @FXML private TableColumn<AlerteStock, String> colStatut;
    @FXML private TableColumn<AlerteStock, Void> colActions;

    @FXML private ComboBox<String> cbFiltreNiveau;
    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private Button btnFiltrer;
    @FXML private Button btnReset;
    @FXML private Button btnRafraichir;
    @FXML private Button btnGenererAlertes; // ✅ AJOUTÉ

    @FXML private Label lblTotalAlertes;
    @FXML private Label lblAlertesRupture;
    @FXML private Label lblAlertesCritiques;
    @FXML private Label lblAlertesFaibles;

    @FXML private CheckBox chkAutoRefresh;

    private ObservableList<AlerteStock> dataAlertes = FXCollections.observableArrayList();
    private ObservableList<AlerteStock> allAlertes = FXCollections.observableArrayList();
    private Timeline autoRefreshTimeline;

    @FXML
    public void initialize() {
        System.out.println("🔄 Initialisation du contrôleur des alertes");
        setupTable();
        setupFiltres();
        setupAutoRefresh();
        chargerAlertes();
    }

    private void setupTable() {
        colNiveau.setCellValueFactory(cellData -> {
            String niveau = cellData.getValue().getNiveauAlerte();
            return new javafx.beans.property.SimpleStringProperty(niveau);
        });
        
        colNiveau.setCellFactory(col -> new TableCell<AlerteStock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "RUPTURE":
                            setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                                   "-fx-background-radius: 12; -fx-padding: 5 10; -fx-font-weight: bold;");
                            break;
                        case "CRITIQUE":
                            setStyle("-fx-background-color: #ff6b35; -fx-text-fill: white; " +
                                   "-fx-background-radius: 12; -fx-padding: 5 10; -fx-font-weight: bold;");
                            break;
                        case "FAIBLE":
                            setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; " +
                                   "-fx-background-radius: 12; -fx-padding: 5 10; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });

        colProduit.setCellValueFactory(cellData -> {
            String nom = cellData.getValue().getProduit() != null 
                ? cellData.getValue().getProduit().getNom() 
                : "N/A";
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        
        colDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDateAlerte();
            String formatted = date != null ? date.format(dateFormatter) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        colStatut.setCellValueFactory(cellData -> {
            String statut = cellData.getValue().getStatut();
            return new javafx.beans.property.SimpleStringProperty(
                "TRAITE".equals(statut) ? "Traitée" : "Non lue"
            );
        });
        
        colStatut.setCellFactory(col -> new TableCell<AlerteStock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Traitée")) {
                        setStyle("-fx-background-color: #28a745; -fx-text-fill: white; " +
                               "-fx-background-radius: 12; -fx-padding: 5 10;");
                    } else {
                        setStyle("-fx-background-color: #ffc107; -fx-text-fill: #333; " +
                               "-fx-background-radius: 12; -fx-padding: 5 10;");
                    }
                }
            }
        });

        colActions.setCellFactory(column -> new TableCell<AlerteStock, Void>() {
            private final Button btnTraiter = new Button("✓ Traiter");
            private final Button btnSupprimer = new Button("🗑️");

            {
                btnTraiter.getStyleClass().add("btn-action-traiter");
                btnSupprimer.getStyleClass().add("btn-action-suppr");

                btnTraiter.setOnAction(e -> {
                    AlerteStock alert = getTableView().getItems().get(getIndex());
                    traiterAlerte(alert);
                });

                btnSupprimer.setOnAction(e -> {
                    AlerteStock alert = getTableView().getItems().get(getIndex());
                    supprimerAlerte(alert);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AlerteStock alert = getTableView().getItems().get(getIndex());
                    if (alert.isTraitee()) {
                        HBox box = new HBox(5, btnSupprimer);
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    } else {
                        HBox box = new HBox(5, btnTraiter, btnSupprimer);
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            }
        });
    }

    private void setupFiltres() {
        cbFiltreNiveau.setItems(FXCollections.observableArrayList(
            "TOUS", "RUPTURE", "CRITIQUE", "FAIBLE"
        ));
        cbFiltreNiveau.setValue("TOUS");

        cbFiltreStatut.setItems(FXCollections.observableArrayList(
            "TOUS", "NON_LU", "TRAITE"
        ));
        cbFiltreStatut.setValue("NON_LU");
    }

    private void setupAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
            if (chkAutoRefresh.isSelected()) {
                System.out.println("🔄 Rafraîchissement automatique...");
                chargerAlertes();
            }
        }));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void chargerAlertes() {
        try {
            System.out.println("📥 Chargement des alertes depuis l'API...");
            List<AlerteStock> alertes = apiGetToutesLesAlertes();
            System.out.println("✅ " + alertes.size() + " alertes récupérées");
            
            allAlertes.setAll(alertes);
            appliquerFiltres();
            calculerStatistiques();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des alertes: " + e.getMessage());
            showError("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFiltrer() {
        System.out.println("🔍 Application des filtres...");
        appliquerFiltres();
    }

    @FXML
    private void handleReset() {
        System.out.println("🔄 Réinitialisation des filtres...");
        cbFiltreNiveau.setValue("TOUS");
        cbFiltreStatut.setValue("NON_LU");
        appliquerFiltres();
    }

    @FXML
    private void handleRafraichir() {
        System.out.println("🔄 Rafraîchissement manuel...");
        chargerAlertes();
    }

    // ✅ MÉTHODE AJOUTÉE - Générer les alertes automatiquement
    @FXML
    private void handleGenererAlertes() {
        try {
            System.out.println("🔔 Génération des alertes automatiques...");
            apiGenererAlertes();
            System.out.println("✅ Alertes générées avec succès");
            chargerAlertes();
            showSuccessDialog("Alertes générées", "Les alertes ont été créées avec succès !\n\nConsultez le tableau ci-dessous.");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération des alertes: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur : " + e.getMessage());
        }
    }

    private void appliquerFiltres() {
        String niveauFiltre = cbFiltreNiveau.getValue();
        String statutFiltre = cbFiltreStatut.getValue();

        ObservableList<AlerteStock> filtrees = FXCollections.observableArrayList();

        for (AlerteStock alert : allAlertes) {
            boolean niveauMatch = niveauFiltre.equals("TOUS") || 
                                 alert.getNiveauAlerte().equals(niveauFiltre);
            boolean statutMatch = statutFiltre.equals("TOUS") ||
                                alert.getStatut().equals(statutFiltre);

            if (niveauMatch && statutMatch) {
                filtrees.add(alert);
            }
        }

        dataAlertes.setAll(filtrees);
        tableAlertes.setItems(dataAlertes);
        
        System.out.println("✅ Filtres appliqués : " + filtrees.size() + " alerte(s)");
    }

    private void calculerStatistiques() {
        long total = allAlertes.stream()
            .filter(a -> "NON_LU".equals(a.getStatut())).count();
        long rupture = allAlertes.stream()
            .filter(a -> "NON_LU".equals(a.getStatut()) && 
                        "RUPTURE".equals(a.getNiveauAlerte())).count();
        long critique = allAlertes.stream()
            .filter(a -> "NON_LU".equals(a.getStatut()) && 
                        "CRITIQUE".equals(a.getNiveauAlerte())).count();
        long faible = allAlertes.stream()
            .filter(a -> "NON_LU".equals(a.getStatut()) && 
                        "FAIBLE".equals(a.getNiveauAlerte())).count();

        lblTotalAlertes.setText(String.valueOf(total));
        lblAlertesRupture.setText(String.valueOf(rupture));
        lblAlertesCritiques.setText(String.valueOf(critique));
        lblAlertesFaibles.setText(String.valueOf(faible));
        
        System.out.println("📊 Statistiques calculées - Total: " + total + ", Rupture: " + rupture + 
                          ", Critique: " + critique + ", Faible: " + faible);
    }

    private void traiterAlerte(AlerteStock alert) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Traiter l'alerte");
        confirm.setHeaderText("Marquer cette alerte comme traitée ?");
        confirm.setContentText(alert.getMessage());
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                apiTraiterAlerte(alert.getId());
                System.out.println("✅ Alerte traitée avec succès");
                chargerAlertes();
            } catch (Exception e) {
                showError("Erreur : " + e.getMessage());
            }
        }
    }

    private void supprimerAlerte(AlerteStock alert) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer cette alerte ?");
        confirm.setContentText("Cette action est irréversible.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                apiSupprimerAlerte(alert.getId());
                System.out.println("✅ Alerte supprimée avec succès");
                chargerAlertes();
            } catch (Exception e) {
                showError("Erreur : " + e.getMessage());
            }
        }
    }

    // ==================== API CALLS ====================

    private List<AlerteStock> apiGetToutesLesAlertes() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("📡 Réponse API - Status: " + res.statusCode());
        System.out.println("📡 Body: " + res.body());
        
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        
        return mapper.readValue(res.body(), new TypeReference<List<AlerteStock>>() {});
    }

    private void apiTraiterAlerte(UUID alertId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + alertId + "/traiter"))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
    }

    private void apiSupprimerAlerte(UUID alertId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + alertId))
                .header("Authorization", "Bearer " + getAuthToken())
                .DELETE()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 204 && res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
    }

    // ✅ MÉTHODE AJOUTÉE - Appel API pour générer les alertes
    private void apiGenererAlertes() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/generer"))
                .header("Authorization", "Bearer " + getAuthToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("📡 Réponse API génération - Status: " + res.statusCode());
        System.out.println("📡 Body: " + res.body());
        
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
    }

    private String getAuthToken() {
        String token = Session.get().getToken();
        System.out.println("🔑 Token utilisé: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        return token;
    }

    // ==================== UTILITAIRES ====================

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ✅ MÉTHODE AJOUTÉE - Afficher un message de succès
    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}