package gestiondestock.controller;

import gestiondestock.model.Stock;
import gestiondestock.service.HttpClientService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditStockController {

    @FXML
    private TextField quantiteField;

    @FXML
    private TextField seuilField;

    private Stock stock;
    private String userRole;

    // callback simple
    private Runnable onSaveCallback;

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    public void setStock(Stock stock) {
        this.stock = stock;

        // remplir les champs
        quantiteField.setText(String.valueOf(stock.getQuantiteDisponible()));
        seuilField.setText(String.valueOf(stock.getSeuilAlerte()));
    }

    public void setUserRole(String role) {
        this.userRole = role;
    }

    @FXML
    private void onSave() {
        try {
            stock.setQuantiteDisponible(Integer.parseInt(quantiteField.getText()));
            stock.setSeuilAlerte(Integer.parseInt(seuilField.getText()));

            // PUT request
            HttpClientService.put(
                    "http://localhost:8080/api/stock/" + stock.getId(),
                    stock,
                    userRole);

            // callback pour recharger la table
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) quantiteField.getScene().getWindow();
        stage.close();
    }
}
