package gestiondestock.controller;

import gestiondestock.model.AuthService;
import gestiondestock.ui.FieldToast;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML private TextField username;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirm;
    @FXML private Button signupButton;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleSignup(ActionEvent e) {
        String u = val(username);
        String em = val(email);
        String p1 = val(password);
        String p2 = val(confirm);

        if (u.isEmpty()) { FieldToast.show(username, "Username is required"); return; }
        if (u.length() < 3) { FieldToast.show(username, "Username must be at least 3 characters"); return; }
        if (!em.isEmpty() && !isValidEmail(em)) { FieldToast.show(email, "Please enter a valid email"); return; }
        if (p1.isEmpty()) { FieldToast.show(password, "Password is required"); return; }
        if (p1.length() < 8) { FieldToast.show(password, "Password must be at least 8 characters"); return; }
        if (!p1.equals(p2)) { FieldToast.show(confirm, "Passwords do not match"); return; }

        signupButton.setDisable(true);
        setError("");
        new Thread(() -> {
            try {
                authService.register(u, p1, em);
                Platform.runLater(() -> {
                    showInfo("Account created", "Your account has been created. Please sign in.");
                    goToLogin(null);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    FieldToast.show(signupButton, ex.getMessage());
                    signupButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    public void goToLogin(ActionEvent e) {
        try {
            Stage stage = (Stage) signupButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            var css = getClass().getResource("/css/login.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
        } catch (Exception ex) {
            showError("Navigation error", ex.getMessage());
        }
    }

    private static String val(TextField tf) { return tf.getText() == null ? "" : tf.getText().trim(); }
    private void setError(String msg) { if (errorLabel != null) errorLabel.setText(msg); }
    private void showInfo(String title, String msg) { show(Alert.AlertType.INFORMATION, title, msg); }
    private void showError(String title, String msg) { show(Alert.AlertType.ERROR, title, msg); }
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }
    private void show(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}