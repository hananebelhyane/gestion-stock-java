package gestiondestock.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GestionDeCommande extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            var css = getClass().getResource("/styles/login.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            var dashCss = getClass().getResource("/styles/dashboard.css");
            if (dashCss != null) {
                scene.getStylesheets().add(dashCss.toExternalForm());
            }
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load login.fxml", e);
        }

    }
}
