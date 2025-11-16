package gestiondestock.controller;

import gestiondestock.model.Produit;
import gestiondestock.model.Stock;
import gestiondestock.service.HttpClientService;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.prefs.Preferences;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.Cursor;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class StockController {

    @FXML
    private TableView<Stock> tableView;

    @FXML
    private TableColumn<Stock, String> produitCol;
    @FXML
    private TableColumn<Stock, Integer> quantiteCol;
    @FXML
    private TableColumn<Stock, Integer> seuilCol;
    @FXML
    private TableColumn<Stock, Void> actionCol;

    @FXML
    private TextField searchField;

    private ObservableList<Stock> list = FXCollections.observableArrayList();

    private static final String API_URL = "http://localhost:8080/api/stock";
    private String userRole;

    @FXML
    public void initialize() {
        loadRole();
        setupColumns();
        setupActions();
        loadData();
        setupSearch();
        produitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduit().getNom()));

        quantiteCol.setCellValueFactory(
                data -> new SimpleIntegerProperty(data.getValue().getQuantiteDisponible()).asObject());

        seuilCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSeuilAlerte()).asObject());
        setupActionColumn();
    }

    private void loadRole() {
        Preferences p = Preferences.userRoot().node("gestion");
        userRole = p.get("role", "NONE");
    }

    private void setupColumns() {

        // ProduitCol : combine Nom | Référence | Catégorie | Prix
        produitCol.setCellValueFactory(cell -> {
            var produit = cell.getValue().getProduit();
            String categorieNom = produit.getCategorie() != null ? produit.getCategorie().getNom() : "";
            String text = produit.getNom() + " | " + produit.getReference()
                    + " | " + categorieNom
                    + " | " + produit.getPrix();
            return new SimpleStringProperty(text);
        });

        // Quantité Disponible
        quantiteCol.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantiteDisponible())
                        .asObject());

        // Seuil d'Alerte
        seuilCol.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getSeuilAlerte()).asObject());
    }

    private void setupActions() {
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Label editIcon = new Label("✏️");
            private final Label deleteIcon = new Label("🗑️");
            private final HBox box = new HBox(10, editIcon, deleteIcon);

            {
                // Style propre, sans rectangle
                editIcon.setStyle("-fx-font-size: 18; -fx-text-fill: grey; -fx-cursor: hand;");
                deleteIcon.setStyle("-fx-font-size: 18; -fx-text-fill: red; -fx-cursor: hand;");

                // Action : éditer
                editIcon.setOnMouseClicked(e -> {
                    Stock s = getTableView().getItems().get(getIndex());
                    editStock(s);
                });

                // Action : supprimer
                deleteIcon.setOnMouseClicked(e -> {
                    Stock s = getTableView().getItems().get(getIndex());
                    deleteStock(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void editStock(Stock stock) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit-stock.fxml"));
            Parent root = loader.load();

            // Récupère le controller du formulaire d'édition
            EditStockController controller = loader.getController();
            controller.setUserRole(userRole);
            controller.setStock(stock);

            // Callback pour recharger la table après sauvegarde
            controller.setOnSaveCallback(this::loadData);

            // Ouvre une nouvelle fenêtre modal
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Éditer Stock");
            stage.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            Stock[] data = HttpClientService.get(API_URL, Stock[].class, userRole);
            list.setAll(List.of(data));
            tableView.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, ov, nv) -> tableView.setItems(list.filtered(
                s -> s.getProduit().getNom().toLowerCase().contains(nv.toLowerCase()))));
    }

    @FXML
    private void onExport() {
        // future code export CSV
    }

    private void setupActionColumn() {

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final FontIcon editIcon = new FontIcon("fas-pencil-alt");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");
            private final HBox box = new HBox(12);

            {
                // CONFIG ICON EDIT
                editIcon.setIconSize(18);
                editIcon.setIconColor(Color.BLUE);
                editIcon.setCursor(Cursor.HAND);

                // CONFIG ICON DELETE
                deleteIcon.setIconSize(18);
                deleteIcon.setIconColor(Color.RED);
                deleteIcon.setCursor(Cursor.HAND);

                // EVENT EDIT
                editIcon.setOnMouseClicked(e -> {
                    Stock stock = getTableView().getItems().get(getIndex());
                    editStock(stock);
                });

                // EVENT DELETE
                deleteIcon.setOnMouseClicked(e -> {
                    Stock stock = getTableView().getItems().get(getIndex());
                    deleteStock(stock);
                });

                box.getChildren().addAll(editIcon, deleteIcon);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    private void deleteStock(Stock stock) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer ce stock ?");
        alert.setContentText("Produit : " + stock.getProduit().getNom());

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                HttpClientService.delete(API_URL + "/" + stock.getId(), userRole);
                list.remove(stock); // retire de la table
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Erreur suppression : " + e.getMessage());
                error.show();
                e.printStackTrace();
            }
        }
    }

}
