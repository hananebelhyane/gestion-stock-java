package gestiondestock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private Button entrerButton;

    @FXML
    void handleEntrer(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage currentStage = (Stage) entrerButton.getScene().getWindow();
        currentStage.close();

        // Ouvrir la fenêtre App
        try {
            Stage appStage = new Stage();
            Group root = new Group();
            Scene scene = new Scene(root, Color.LIGHTSKYBLUE);
            Image logo = new Image("/photos/logo.png");
            appStage.getIcons().add(logo);
            appStage.setTitle("Gestion de stock");
            appStage.setWidth(1000);
            appStage.setHeight(600);
            appStage.setResizable(false);

            Text text = new Text();
            text.setText("Welcome to the Commande Gestion App");
            text.setX(5);
            text.setY(70);
            root.getChildren().add(text);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 50));

            appStage.setScene(scene);
            appStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}