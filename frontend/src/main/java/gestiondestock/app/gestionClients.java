package gestiondestock.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class gestionClients extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/client-view.fxml"));
        Scene scene = new Scene(root);
        
        // Charger le CSS
        String css = getClass().getResource("/css/client-style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Clients");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}