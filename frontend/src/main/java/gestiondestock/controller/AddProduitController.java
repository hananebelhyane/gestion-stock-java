package gestiondestock.controller;

import gestiondestock.dao.ProduitDAO;
import gestiondestock.model.Produit;
import gestiondestock.model.Categorie;
import gestiondestock.model.Fournisseur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddProduitController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private ComboBox<String> fournisseurCombo;
    @FXML private TextArea descriptionArea;

    private ProduitController produitController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupValidation();
    }

    private void setupComboBoxes() {
        // Peupler les combobox avec des données
        categorieCombo.getItems().addAll(
            "Électronique", "Informatique", "Mobilier", "Bureautique", 
            "Alimentaire", "Vêtements", "Autre"
        );
        
        fournisseurCombo.getItems().addAll(
            "TechCorp", "PhoneDistri", "OfficePlus", "SoundTech",
            "FurnitureCo", "GeneralSupplies", "Autre"
        );
    }

    private void setupValidation() {
        // Validation numérique pour le prix
        prixField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                prixField.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleAddProduit() {
        if (!validateForm()) {
            return;
        }

        try {
            Produit newProduit = createProduitFromForm();
            ProduitDAO.save(newProduit);
            
            if (produitController != null) {
                produitController.refreshTable();
            }
            
            showAlert("Succès", "Produit ajouté avec succès!", Alert.AlertType.INFORMATION);
            closeWindow();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private Produit createProduitFromForm() {
        String nom = nomField.getText().trim();
        String description = descriptionArea.getText().trim();
        double prix = Double.parseDouble(prixField.getText());
        
        Categorie categorie = new Categorie(categorieCombo.getValue(), "");
        Fournisseur fournisseur = new Fournisseur(fournisseurCombo.getValue(), "", "", "");
        
        return new Produit(nom, description, prix, "", categorie, fournisseur);
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            errors.append("- Le nom est obligatoire\n");
        }
        
        if (prixField.getText() == null || prixField.getText().trim().isEmpty()) {
            errors.append("- Le prix est obligatoire\n");
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText());
                if (prix <= 0) {
                    errors.append("- Le prix doit être supérieur à 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Le prix doit être un nombre valide\n");
            }
        }
        
        if (categorieCombo.getValue() == null) {
            errors.append("- La catégorie est obligatoire\n");
        }
        
        if (fournisseurCombo.getValue() == null) {
            errors.append("- Le fournisseur est obligatoire\n");
        }
        
        if (errors.length() > 0) {
            showAlert("Erreur de validation", "Veuillez corriger les erreurs suivantes:\n" + errors, Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }

    public void setProduitController(ProduitController controller) {
        this.produitController = controller;
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}