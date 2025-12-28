package gestiondestock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gestiondestock.model.Session;
import gestiondestock.model.Stock;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;

import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StockController {

    private static final String BASE_URL = "http://localhost:8080/api/stock";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private TableView<Stock> tableStock;
    @FXML
    private TableColumn<Stock, String> colId;
    @FXML
    private TableColumn<Stock, String> colProduit;
    @FXML
    private TableColumn<Stock, Integer> colQte;
    @FXML
    private TableColumn<Stock, Double> colPrix;
    @FXML
    private TableColumn<Stock, Void> colActions;

    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnRechercher;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnExporter;
    @FXML
    private Button btnRafraichir;

    private ObservableList<Stock> data = FXCollections.observableArrayList();
    private ObservableList<Stock> allData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStockData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> {
            UUID id = cellData.getValue().getId();
            return new javafx.beans.property.SimpleStringProperty(
                    id != null ? id.toString().substring(0, 8) + "..." : "N/A");
        });

        colProduit.setCellValueFactory(cellData -> {
            String nom = cellData.getValue().getProduit() != null ? cellData.getValue().getProduit().getNom() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        colQte.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
        colQte.setCellFactory(col -> new TableCell<Stock, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Stock stock = getTableView().getItems().get(getIndex());
                    if (stock.getSeuilAlerte() != null && item <= stock.getSeuilAlerte()) {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    }
                }
            }
        });

        colPrix.setCellValueFactory(cellData -> {
            Double prix = cellData.getValue().getProduit() != null &&
                    cellData.getValue().getProduit().getPrixUnitaire() != null
                            ? cellData.getValue().getProduit().getPrixUnitaire()
                            : 0.0;
            return new javafx.beans.property.SimpleObjectProperty<>(prix);
        });

        colActions.setCellFactory(column -> new TableCell<Stock, Void>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");

            {
                btnEdit.getStyleClass().add("action-btn");
                btnDelete.getStyleClass().add("action-btn");

                btnEdit.setOnAction(e -> {
                    Stock stock = getTableView().getItems().get(getIndex());
                    modifierStock(stock);
                });

                btnDelete.setOnAction(e -> {
                    Stock stock = getTableView().getItems().get(getIndex());
                    supprimerStock(stock);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                HBox box = new HBox(10, btnEdit, btnDelete);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });
    }

    // ================= HTTP Services =================

    private List<Stock> apiGetAllStock() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + getAuthToken())
                .GET()
                .build();
                //debug
                  System.out.println("TOKEN ENVOYÉ = " + getAuthToken());

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());

        return mapper.readValue(res.body(), new TypeReference<List<Stock>>() {
        });
    }

    private void apiDeleteStock(UUID id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Authorization", "Bearer " + getAuthToken())
                .DELETE()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200 && res.statusCode() != 204)
            throw new Exception("Erreur HTTP: " + res.statusCode());
    }

    private Stock apiUpdateStock(UUID id, Stock stock) throws Exception {
        String json = mapper.writeValueAsString(stock);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAuthToken())
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new Exception("Erreur HTTP: " + res.statusCode());
        return mapper.readValue(res.body(), Stock.class);
    }

    private String getAuthToken() {
        return Session.get().getToken();
    }

    // ================= JAVA FX HANDLERS =================

    private void loadStockData() {
        try {
            List<Stock> stocks = apiGetAllStock();
            allData.setAll(stocks);
            data.setAll(stocks);
            tableStock.setItems(data);
            showInfo("Données chargées : " + stocks.size() + " stocks");
        } catch (Exception e) {
            showError("Erreur de chargement : " + e.getMessage());
        }
    }

    private void supprimerStock(Stock stock) {
        if (stock == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer ce stock ?");
        confirm.setContentText("Êtes-vous sûr ?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                apiDeleteStock(stock.getId());
                data.remove(stock);
                allData.remove(stock);
                showInfo("Stock supprimé avec succès");
            } catch (Exception e) {
                showError("Erreur suppression : " + e.getMessage());
            }
        }
    }

    private void modifierStock(Stock stock) {
        if (stock == null)
            return;

        TextInputDialog dialog = new TextInputDialog(String.valueOf(stock.getQuantiteDisponible()));
        dialog.setTitle("Modifier Stock");
        dialog.setHeaderText("Modifier la quantité");
        dialog.setContentText("Nouvelle quantité :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qte -> {
            try {
                int newQte = Integer.parseInt(qte);
                stock.setQuantiteDisponible(newQte);
                apiUpdateStock(stock.getId(), stock);
                tableStock.refresh();
                showInfo("Stock mis à jour avec succès");
            } catch (NumberFormatException e) {
                showError("Quantité invalide");
            } catch (Exception e) {
                showError("Erreur mise à jour : " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleRechercher() {
        String filtre = txtSearch.getText().toLowerCase().trim();
        if (filtre.isEmpty()) {
            tableStock.setItems(allData);
            return;
        }

        ObservableList<Stock> filtré = FXCollections.observableArrayList();
        for (Stock s : allData) {
            if (s.getProduit() != null && s.getProduit().getNom() != null &&
                    s.getProduit().getNom().toLowerCase().contains(filtre)) {
                filtré.add(s);
            }
        }
        tableStock.setItems(filtré);
        showInfo("Résultats trouvés : " + filtré.size());
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStockDialog.fxml"));
            Parent root = loader.load(); // Cast en Parent
            AddStockDialogController controller = loader.getController();
            // Corrige le Consumer<Void> en lambda
            controller.setOnSuccess(v -> loadStockData());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un stock");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAjouter.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            showError("Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    @FXML
    private void rafraichir() {
        loadStockData();
    }

    // ================= Utils =================

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

    @FXML
    private void handleExporter() {
        try {
            // Ouvrir une boîte de dialogue "Enregistrer sous"
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les stocks");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));
            fileChooser.setInitialFileName("stocks.csv");

            Stage stage = (Stage) btnExporter.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file == null)
                return; // utilisateur a annulé

            // Construire le contenu CSV avec encodage UTF-8 et point-virgule
            StringBuilder sb = new StringBuilder();
            // Ajouter le BOM UTF-8 pour Excel
            sb.append('\ufeff');
            
            // En-tête avec point-virgule (meilleur pour Excel)
            sb.append("ID;Produit;Quantité;Prix (DH);Seuil Alerte\n");

            for (Stock s : tableStock.getItems()) {
                // Échapper et entourer de guillemets
                String id = escapeCSV(s.getId() != null ? s.getId().toString() : "N/A");
                String produit = escapeCSV(s.getProduit() != null ? s.getProduit().getNom() : "N/A");
                String quantite = String.valueOf(s.getQuantiteDisponible() != null ? s.getQuantiteDisponible() : 0);
                String prix = String.format("%.2f", s.getProduit() != null && s.getProduit().getPrixUnitaire() != null ? s.getProduit().getPrixUnitaire() : 0.0);
                String seuil = String.valueOf(s.getSeuilAlerte() != null ? s.getSeuilAlerte() : "N/A");
                
                sb.append(id).append(";");
                sb.append(produit).append(";");
                sb.append(quantite).append(";");
                sb.append(prix).append(";");
                sb.append(seuil).append("\n");
            }

            // Écrire dans le fichier avec UTF-8
            Files.writeString(file.toPath(), sb.toString(), java.nio.charset.StandardCharsets.UTF_8);

            showInfo("Export réussi :\n" + file.getAbsolutePath());

        } catch (Exception e) {
            showError("Erreur d'export : " + e.getMessage());
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "\"\"";
        // Remplacer les guillemets par des guillemets doubles et entourer de guillemets
        if (value.contains("\"") || value.contains(";") || value.contains("\n") || value.contains(",")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return "\"" + value + "\"";
    }

}
