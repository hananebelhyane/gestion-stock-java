package gestiondestock.controller;

import gestiondestock.model.AdminProfileModel;
import gestiondestock.model.Session;
import gestiondestock.service.AdminProfileService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminProfileController {
    
    private static final Logger LOGGER = Logger.getLogger(AdminProfileController.class.getName());

    @FXML private Label lblUsername;
    @FXML private Label lblRole;
    @FXML private Label lblNom;
    @FXML private Label lblPrenom;
    @FXML private Label lblEmail;
    @FXML private Label lblTelephone;
    @FXML private Label lblAvatar;
    
    @FXML private Button btnEditProfile;
    @FXML private Button btnChangePassword;

    private final AdminProfileService adminProfileService = new AdminProfileService();
    private AdminProfileModel currentProfile;

    @FXML
    public void initialize() {
        loadProfile();
        
        // Initialiser l'avatar avec la première lettre du username
        String username = Session.get().getUsername();
        if (username != null && !username.isEmpty() && lblAvatar != null) {
            lblAvatar.setText(username.substring(0, 1).toUpperCase());
        }
    }

    private void loadProfile() {
        try {
            // Récupérer le profil depuis le backend
            currentProfile = adminProfileService.getMyProfile();
            
            if (lblUsername != null) lblUsername.setText(currentProfile.getUsername());
            if (lblRole != null) lblRole.setText(Session.get().getRole());
            if (lblNom != null) lblNom.setText(currentProfile.getNom() != null ? currentProfile.getNom() : "Non disponible");
            if (lblPrenom != null) lblPrenom.setText(currentProfile.getPrenom() != null ? currentProfile.getPrenom() : "Non disponible");
            if (lblEmail != null) lblEmail.setText(currentProfile.getEmail() != null ? currentProfile.getEmail() : "Non disponible");
            if (lblTelephone != null) lblTelephone.setText(currentProfile.getTelephone() != null ? currentProfile.getTelephone() : "Non disponible");
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du profil", ex);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le profil: " + ex.getMessage());
        }
    }

    @FXML
    private void handleEditProfile() {
        Dialog<AdminProfileModel> dialog = createEditProfileDialog();
        Optional<AdminProfileModel> result = dialog.showAndWait();

        result.ifPresent(profile -> {
            try {
                // Appeler le service pour mettre à jour le profil
                AdminProfileModel updatedProfile = adminProfileService.updateProfile(profile);
                currentProfile = updatedProfile;
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès!");
                loadProfile();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du profil", ex);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le profil: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void handleChangePassword() {
        Dialog<String[]> dialog = createChangePasswordDialog();
        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(passwords -> {
            try {
                String oldPassword = passwords[0];
                String newPassword = passwords[1];
                
                // Appeler le service pour changer le mot de passe
                adminProfileService.changePassword(oldPassword, newPassword);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe changé avec succès!");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors du changement de mot de passe", ex);
                showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
            }
        });
    }

    private Dialog<AdminProfileModel> createEditProfileDialog() {
        Dialog<AdminProfileModel> dialog = new Dialog<>();
        dialog.setTitle("Modifier le profil");
        dialog.setHeaderText("Modifier vos informations personnelles");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNom = new TextField();
        TextField txtPrenom = new TextField();
        TextField txtEmail = new TextField();
        TextField txtTelephone = new TextField();

        txtNom.setPromptText("Nom");
        txtPrenom.setPromptText("Prénom");
        txtEmail.setPromptText("Email");
        txtTelephone.setPromptText("Téléphone");

        // Remplir avec les valeurs actuelles
        if (currentProfile != null) {
            txtNom.setText(currentProfile.getNom() != null ? currentProfile.getNom() : "");
            txtPrenom.setText(currentProfile.getPrenom() != null ? currentProfile.getPrenom() : "");
            txtEmail.setText(currentProfile.getEmail() != null ? currentProfile.getEmail() : "");
            txtTelephone.setText(currentProfile.getTelephone() != null ? currentProfile.getTelephone() : "");
        }

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(txtPrenom, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(txtTelephone, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!validateProfileFields(txtNom, txtPrenom, txtEmail, txtTelephone)) {
                    return null;
                }
                AdminProfileModel newProfile = new AdminProfileModel();
                newProfile.setNom(txtNom.getText().trim());
                newProfile.setPrenom(txtPrenom.getText().trim());
                newProfile.setEmail(txtEmail.getText().trim());
                newProfile.setTelephone(txtTelephone.getText().trim());
                newProfile.setUsername(Session.get().getUsername());
                return newProfile;
            }
            return null;
        });

        return dialog;
    }

    private Dialog<String[]> createChangePasswordDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Changer le mot de passe");
        dialog.setHeaderText("Modifier votre mot de passe");

        ButtonType saveButtonType = new ButtonType("Changer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField txtOldPassword = new PasswordField();
        PasswordField txtNewPassword = new PasswordField();
        PasswordField txtConfirmPassword = new PasswordField();

        txtOldPassword.setPromptText("Ancien mot de passe");
        txtNewPassword.setPromptText("Nouveau mot de passe");
        txtConfirmPassword.setPromptText("Confirmer le mot de passe");

        grid.add(new Label("Ancien mot de passe:"), 0, 0);
        grid.add(txtOldPassword, 1, 0);
        grid.add(new Label("Nouveau mot de passe:"), 0, 1);
        grid.add(txtNewPassword, 1, 1);
        grid.add(new Label("Confirmer:"), 0, 2);
        grid.add(txtConfirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String oldPwd = txtOldPassword.getText();
                String newPwd = txtNewPassword.getText();
                String confirmPwd = txtConfirmPassword.getText();

                if (oldPwd.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "L'ancien mot de passe est requis");
                    return null;
                }
                if (newPwd.length() < 8) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "Le nouveau mot de passe doit contenir au moins 8 caractères");
                    return null;
                }
                if (!newPwd.equals(confirmPwd)) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "Les mots de passe ne correspondent pas");
                    return null;
                }

                return new String[]{oldPwd, newPwd};
            }
            return null;
        });

        return dialog;
    }

    private boolean validateProfileFields(TextField txtNom, TextField txtPrenom, 
                                         TextField txtEmail, TextField txtTelephone) {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() || txtTelephone.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Tous les champs sont obligatoires");
            return false;
        }

        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Format d'email invalide");
            return false;
        }

        if (!txtTelephone.getText().matches("^[0-9+\\-\\s()]{10,20}$")) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Format de téléphone invalide");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
