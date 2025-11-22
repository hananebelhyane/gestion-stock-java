package gestiondestock.app;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Welcome extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Démarrer avec la page de login
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            
            // Charger le CSS du login
            var css = getClass().getResource("/styles/login.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            
            primaryStage.setTitle("Gestion de Stock - Connexion");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de login.fxml", e);
        }
    }
}