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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import gestiondestock.ui.FieldToast;

public class LoginController {

    @FXML
    private TextField userusername;
    @FXML
    private PasswordField usermdp;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Simple inline checks only.
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String username = userusername.getText() == null ? "" : userusername.getText().trim();
        String password = usermdp.getText() == null ? "" : usermdp.getText();

        // Client-side validation
        if (username.isEmpty()) {
            FieldToast.show(userusername, "Username is required");
            return;
        }
        if (username.length() < 3) {
            FieldToast.show(userusername, "Username must be at least 3 characters");
            return;
        }
        if (password.isEmpty()) {
            FieldToast.show(usermdp, "Password is required");
            return;
        }
        if (password.length() < 8) {
            FieldToast.show(usermdp, "Password must be at least 8 characters");
            return;
        }
        clearError();

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
                    FieldToast.show(usermdp, ex.getMessage());
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }

    private void switchToView() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/layoutBar.fxml"));
            Scene scene = new Scene(root);
            var css = getClass().getResource("/styles/login.css");
            if (css != null)
                scene.getStylesheets().add(css.toExternalForm());
            // Add dashboard stylesheet so dashboard view uses its styles
            var dashCss = getClass().getResource("/styles/dashboard.css");
            if (dashCss != null)
                scene.getStylesheets().add(dashCss.toExternalForm());
            stage.setScene(scene);
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

    private void setError(String msg) {
        if (errorLabel != null)
            errorLabel.setText(msg);
    }

    private void clearError() {
        if (errorLabel != null)
            errorLabel.setText("");
    }

    @FXML
    public void goToSignup(javafx.event.ActionEvent e) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Scene scene = new Scene(root);
            var css = getClass().getResource("/styles/login.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Navigation error", ex.getMessage());
        }
    }
}
