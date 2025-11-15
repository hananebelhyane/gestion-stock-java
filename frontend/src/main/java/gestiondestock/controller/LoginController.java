package gestiondestock.controller;

import gestiondestock.model.AuthResponse;
import gestiondestock.model.AuthService;
import gestiondestock.model.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField userusername;
    @FXML private PasswordField usermdp;
    @FXML private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Simple inline checks only.
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String username = userusername.getText() == null ? "" : userusername.getText().trim();
        String password = usermdp.getText() == null ? "" : usermdp.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing info", "Please fill in username and password.");
            return;
        }

        loginButton.setDisable(true);
        new Thread(() -> {
            try {
                AuthResponse res = authService.login(username, password);
                Session.get().setToken(res.getToken());
                Session.get().setRole(res.getRole());
                Session.get().setUsername(res.getUsername());
                Platform.runLater(this::switchToView);
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Login failed", ex.getMessage());
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }

    private void switchToView() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/view.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Navigation error", "Cannot open view: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
