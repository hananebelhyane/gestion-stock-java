package gestiondestock.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class gestionMagasiniers extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/magasinier-view.fxml"));
        Scene scene = new Scene(root);
        
        // Charger le CSS
        String css = getClass().getResource("/css/magasinier-style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Magasiniers");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}