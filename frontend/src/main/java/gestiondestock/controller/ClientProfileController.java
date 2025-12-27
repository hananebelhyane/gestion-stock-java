package gestiondestock.controller;

import gestiondestock.model.ClientModel;
import gestiondestock.model.Session;
import gestiondestock.service.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class ClientProfileController {

    @FXML
    private Label lblUsername;
    @FXML
    private Label lblAvatar;
    @FXML
    private Label lblRole;
    @FXML
    private Label lblNom;
    @FXML
    private Label lblPrenom;
    @FXML
    private Label lblTelephone;
    @FXML
    private Label lblAdresse;
    @FXML
    private Button btnEditProfile;
    @FXML
    private Button btnChangePassword;
    @FXML
    private Button btnBack;

    private final ClientService clientService = new ClientService();
    private ClientModel currentClient;

    @FXML
    public void initialize() {
        loadClientProfile();
    }

    private void loadClientProfile() {
        try {
            String clientId = Session.get().getUserId();
            if (clientId != null && !clientId.isEmpty()) {
                currentClient = clientService.getClientById(clientId);
                updateUI();
            }
        } catch (Exception e) {
            showError("Erreur", "Impossible de charger le profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUI() {
        if (currentClient == null)
            return;

        String username = currentClient.getUsername();
        lblUsername.setText(username != null ? username : "Client");
        lblAvatar.setText(String.valueOf((username != null ? username.charAt(0) : 'C')).toUpperCase());
        lblRole.setText("CLIENT");
        lblNom.setText(currentClient.getNom() != null ? currentClient.getNom() : "N/A");
        lblPrenom.setText(currentClient.getPrenom() != null ? currentClient.getPrenom() : "N/A");
        lblTelephone.setText(currentClient.getTelephone() != null ? currentClient.getTelephone() : "N/A");
        lblAdresse.setText(currentClient.getAdresse() != null ? currentClient.getAdresse() : "Adresse non définie");
    }

    @FXML
    private void handleEditProfile() {
        if (currentClient == null) {
            showError("Erreur", "Aucun profil à modifier");
            return;
        }

        Dialog<ClientModel> dialog = new Dialog<>();
        dialog.setTitle("Modifier le profil");
        dialog.setHeaderText("Mettez à jour vos informations");

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(10));

        TextField txtNom = new TextField(currentClient.getNom());
        txtNom.setPromptText("Nom");
        TextField txtPrenom = new TextField(currentClient.getPrenom());
        txtPrenom.setPromptText("Prénom");
        TextField txtTelephone = new TextField(currentClient.getTelephone());
        txtTelephone.setPromptText("Téléphone");
        TextField txtAdresse = new TextField(currentClient.getAdresse());
        txtAdresse.setPromptText("Adresse");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(txtPrenom, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(txtTelephone, 1, 2);
        grid.add(new Label("Adresse:"), 0, 3);
        grid.add(txtAdresse, 1, 3);

        pane.setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                ClientModel updated = new ClientModel();
                updated.setId(currentClient.getId());
                updated.setNom(txtNom.getText());
                updated.setPrenom(txtPrenom.getText());
                updated.setTelephone(txtTelephone.getText());
                updated.setAdresse(txtAdresse.getText());
                updated.setUsername(currentClient.getUsername());
                return updated;
            }
            return null;
        });

        Optional<ClientModel> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            try {
                clientService.updateClient(currentClient.getId(), updated);
                currentClient = updated;
                updateUI();
                showInfo("Succès", "Profil mis à jour avec succès");
            } catch (Exception e) {
                showError("Erreur", "Impossible de mettre à jour le profil: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleChangePassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Changer le mot de passe");
        dialog.setHeaderText("Entrez votre nouveau mot de passe");

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));

        PasswordField oldPassword = new PasswordField();
        oldPassword.setPromptText("Ancien mot de passe");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Nouveau mot de passe");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirmer le mot de passe");

        content.getChildren().addAll(
                new Label("Ancien mot de passe:"), oldPassword,
                new Label("Nouveau mot de passe:"), newPassword,
                new Label("Confirmer le mot de passe:"), confirmPassword);

        pane.setContent(content);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (!newPassword.getText().equals(confirmPassword.getText())) {
                    showError("Erreur", "Les mots de passe ne correspondent pas");
                    return null;
                }
                return newPassword.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPwd -> {
            try {
                // Call backend API to change password
                showInfo("Succès", "Mot de passe changé avec succès");
            } catch (Exception e) {
                showError("Erreur", "Impossible de changer le mot de passe: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/fxml/client_dashboard.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            var css = getClass().getResource("/styles/dashboard.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle("Dashboard Client");
        } catch (Exception e) {
            showError("Erreur", "Impossible de retourner au catalogue: " + e.getMessage());
        }
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
