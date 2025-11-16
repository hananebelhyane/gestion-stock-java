package gestiondestock.controller;

import gestiondestock.model.Produit;
import gestiondestock.model.Categorie;
import gestiondestock.model.Fournisseur;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditProduitController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private ComboBox<String> fournisseurCombo;
    @FXML private TextArea descriptionArea;

    private ProduitController produitController;
    private Produit produit;

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

    public void setProduit(Produit produit) {
        this.produit = produit;
        populateForm();
    }

    private void populateForm() {
        if (produit != null) {
            nomField.setText(produit.getNom());
            descriptionArea.setText(produit.getDescription());
            prixField.setText(String.valueOf(produit.getPrixUnitaire()));
            
            if (produit.getCategorie() != null) {
                categorieCombo.setValue(produit.getCategorie().getNom());
            }
            if (produit.getFournisseur() != null) {
                fournisseurCombo.setValue(produit.getFournisseur().getNom());
            }
        }
    }

    @FXML
    private void handleUpdateProduit() {
        if (!validateForm()) {
            return;
        }

        try {
            updateProduitFromForm();
            
            // Dans une vraie application, vous appelleriez ProduitDAO.update(produit)
            // Pour l'instant, on rafraîchit juste la table
            if (produitController != null) {
                produitController.refreshTable();
            }
            
            showAlert("Succès", "Produit modifié avec succès!", Alert.AlertType.INFORMATION);
            closeWindow();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        if (produit == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le produit \"" + produit.getNom() + "\" ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Supprimer le produit de la liste
                    // Dans une vraie application: ProduitDAO.delete(produit.getId());
                    if (produitController != null) {
                        produitController.refreshTable();
                    }
                    showAlert("Succès", "Produit supprimé avec succès!", Alert.AlertType.INFORMATION);
                    closeWindow();
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void updateProduitFromForm() {
        produit.setNom(nomField.getText().trim());
        produit.setDescription(descriptionArea.getText().trim());
        try {
            produit.setPrixUnitaire(Double.valueOf(prixField.getText()));
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format de prix invalide", Alert.AlertType.ERROR);
        }
        
        Categorie newCategorie = new Categorie(categorieCombo.getValue(), "");
        Fournisseur newFournisseur = new Fournisseur(fournisseurCombo.getValue(), "", "", "");
        
        produit.setCategorie(newCategorie);
        produit.setFournisseur(newFournisseur);
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