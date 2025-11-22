package gestiondestock.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class gestionFournisseurs extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/fournisseur-view.fxml"));
        Scene scene = new Scene(root);
        
        // Charger le CSS
        String css = getClass().getResource("/css/fournisseur-style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Fournisseurs");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
