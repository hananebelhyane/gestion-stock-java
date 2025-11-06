package gestiondestock;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class App extends Application    
{
    public static void main( String[] args)
    {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Group root= new Group();
        Scene scene = new Scene(root,Color.LIGHTSKYBLUE);
        Image logo= new Image("/photos/logo.png");
        stage.getIcons().add(logo);
        stage.setTitle("Gestion de stock");
        stage.setWidth(1000);
        stage.setHeight(600);
        // stage.setX(1);
        // stage.setY(1);
        //stage.setFullScreen(true);

        stage.setResizable(false);
        

        Text text=new Text();
        text.setText("Welcome to the Commande Gestion App");
        text.setX(5);
        text.setY(70);
        root.getChildren().add(text);
        text.setFont(Font.font("Arial",FontWeight.BOLD,50));

        stage.setScene(scene);
        stage.show();
    }
}