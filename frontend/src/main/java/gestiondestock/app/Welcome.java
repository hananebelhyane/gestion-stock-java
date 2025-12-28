package gestiondestock.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Welcome extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root);
        
        // Charger le CSS
        var css = getClass().getResource("/styles/login.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login - Gestion de Stock");
        
        // Définir une taille raisonnable et centrer la fenêtre
        primaryStage.setWidth(1000);
        primaryStage.setHeight(650);
        primaryStage.centerOnScreen();
        
        primaryStage.show();
    }
}