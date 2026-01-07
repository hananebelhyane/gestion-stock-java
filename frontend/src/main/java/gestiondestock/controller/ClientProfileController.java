package gestiondestock.controller;

import gestiondestock.model.ClientProfileModel;
import gestiondestock.service.ClientProfileService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientProfileController {

    private static final Logger LOGGER = Logger.getLogger(ClientProfileController.class.getName());

    @FXML
    private Label lblUsername;
    @FXML
    private Label lblFullName;
    @FXML
    private Label lblTelephone;
    @FXML
    private Label lblAdresse;
    @FXML
    private Label lblAvatar;

    private final ClientProfileService clientProfileService = new ClientProfileService();
    private ClientProfileModel currentProfile;

    @FXML
    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        new Thread(() -> {
            try {
                currentProfile = clientProfileService.getMyProfile();

                Platform.runLater(() -> {
                    if (lblUsername != null)
                        lblUsername.setText(currentProfile.getUsername());

                    if (lblFullName != null) {
                        String prenom = currentProfile.getPrenom() != null ? currentProfile.getPrenom() : "";
                        String nom = currentProfile.getNom() != null ? currentProfile.getNom() : "";
                        lblFullName.setText((prenom + " " + nom).trim());
                    }

                    if (lblTelephone != null)
                        lblTelephone.setText(currentProfile.getTelephone() != null ? currentProfile.getTelephone()
                                : "Non disponible");

                    if (lblAdresse != null)
                        lblAdresse.setText(
                                currentProfile.getAdresse() != null ? currentProfile.getAdresse() : "Non définie");

                    if (lblAvatar != null && currentProfile.getUsername() != null
                            && !currentProfile.getUsername().isEmpty()) {
                        lblAvatar.setText(currentProfile.getUsername().substring(0, 1).toUpperCase());
                    }
                });
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Erreur chargement profil", ex);
                Platform.runLater(
                        () -> showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données."));
            }
        }).start();
    }

    @FXML
    public void handleEditProfile() {
        Dialog<ClientProfileModel> dialog = createEditProfileDialog();
        Button btnSave = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (btnSave != null) {
            btnSave.setText("Enregistrer les Modifications");
            btnSave.setStyle(
                    "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        }

        Optional<ClientProfileModel> result = dialog.showAndWait();
        result.ifPresent(profile -> {
            try {
                clientProfileService.updateProfile(profile);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour.");
                loadProfile();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour.");
            }
        });
    }

    private Dialog<ClientProfileModel> createEditProfileDialog() {
        Dialog<ClientProfileModel> dialog = new Dialog<>();
        dialog.setTitle("Modification du Profil");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField txtNom = new TextField(currentProfile.getNom());
        TextField txtPrenom = new TextField(currentProfile.getPrenom());
        TextField txtTel = new TextField(currentProfile.getTelephone());
        TextField txtAdr = new TextField(currentProfile.getAdresse());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(txtPrenom, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(txtTel, 1, 2);
        grid.add(new Label("Adresse:"), 0, 3);
        grid.add(txtAdr, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                currentProfile.setNom(txtNom.getText());
                currentProfile.setPrenom(txtPrenom.getText());
                currentProfile.setTelephone(txtTel.getText());
                currentProfile.setAdresse(txtAdr.getText());
                return currentProfile;
            }
            return null;
        });
        return dialog;
    }

    @FXML
    public void handleChangePassword() {
        Dialog<String[]> dialog = createChangePasswordDialog();
        Button btnConfirm = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (btnConfirm != null) {
            btnConfirm.setText("Modifier le Mot de Passe");
            btnConfirm.setStyle(
                    "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        }

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(pass -> {
            try {
                clientProfileService.changePassword(pass[0], pass[1]);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe modifié.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
            }
        });
    }

    private Dialog<String[]> createChangePasswordDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Sécurité");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        PasswordField oldP = new PasswordField();
        PasswordField newP = new PasswordField();
        PasswordField confP = new PasswordField();

        box.getChildren().addAll(new Label("Ancien"), oldP, new Label("Nouveau"), newP, new Label("Confirmer"), confP);

        dialog.getDialogPane().setContent(box);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK)
                return new String[] { oldP.getText(), newP.getText() };
            return null;
        });
        return dialog;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}