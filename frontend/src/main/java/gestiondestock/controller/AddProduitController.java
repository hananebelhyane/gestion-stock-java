package gestiondestock.controller;

import gestiondestock.model.Produit;
import gestiondestock.model.Categorie;
import gestiondestock.model.Fournisseur;
import gestiondestock.service.ProduitService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddProduitController {

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private ComboBox<String> fournisseurCombo;
    @FXML private TextArea descriptionArea;

    private ProduitController produitController;
    private final ProduitService produitService = new ProduitService();

    @FXML
    private void initialize() {
        categorieCombo.getItems().addAll("Électronique", "Informatique", "Mobilier", "Bureautique", "Alimentaire", "Vêtements", "Autre");
        fournisseurCombo.getItems().addAll("TechCorp", "PhoneDistri", "OfficePlus", "SoundTech","FurnitureCo", "GeneralSupplies", "Autre");

        prixField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) prixField.setText(oldValue);
        });
    }

    @FXML
    private void handleAddProduit() {
        if (!validateForm()) return;

        Produit produit = new Produit(
                nomField.getText().trim(),
                descriptionArea.getText().trim(),
                Double.parseDouble(prixField.getText()),
                "",
                new Categorie(categorieCombo.getValue(), ""),
                new Fournisseur(fournisseurCombo.getValue(), "", "", "")
        );

        javafx.concurrent.Task<Produit> task = new javafx.concurrent.Task<>() {
            @Override
            protected Produit call() throws Exception {
                return produitService.addProduit(produit);
            }
        };

        task.setOnSucceeded(evt -> {
            if (produitController != null) produitController.refreshTable();
            showAlert("Succès", "Produit ajouté avec succès!", Alert.AlertType.INFORMATION);
            closeWindow();
        });

        task.setOnFailed(evt -> showAlert("Erreur", "Erreur lors de l'ajout: " + task.getException().getMessage(), Alert.AlertType.ERROR));

        new Thread(task).start();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        if (nomField.getText().isBlank()) errors.append("- Le nom est obligatoire\n");
        if (prixField.getText().isBlank()) errors.append("- Le prix est obligatoire\n");
        else {
            try { if (Double.parseDouble(prixField.getText()) <= 0) errors.append("- Le prix doit être > 0\n"); }
            catch (NumberFormatException e) { errors.append("- Prix invalide\n"); }
        }
        if (categorieCombo.getValue() == null) errors.append("- La catégorie est obligatoire\n");
        if (fournisseurCombo.getValue() == null) errors.append("- Le fournisseur est obligatoire\n");
        if (errors.length() > 0) { showAlert("Erreur de validation", errors.toString(), Alert.AlertType.ERROR); return false; }
        return true;
    }

    public void setProduitController(ProduitController controller) { this.produitController = controller; }

    private void closeWindow() { ((Stage) nomField.getScene().getWindow()).close(); }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }
}
