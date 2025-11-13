package gestiondestock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import gestiondestock.model.Admin;

public class LoginController {

    @FXML
    private TextField userfirstname;

    @FXML
    private TextField userlastname;

    @FXML
    private TextField useremail;

    @FXML
    private TextField userusername;

    @FXML
    private TextField usertelephone;

    @FXML
    private TextField usermdp;

    @FXML
    private Button loginButton;

    @FXML
    private Button googleLoginButton;

    @FXML
    public void handleGoogleLogin(ActionEvent event) {
        // Handle Google OAuth login
        System.out.println("Google OAuth login initiated");
        
        // For now, simulate a successful login with demo user
        // In a real implementation, this would integrate with Google OAuth 2.0
        try {
            String prenom = "Demo";
            String nom = "User";
            
            // Close the login window
            Stage currentStage = (Stage) googleLoginButton.getScene().getWindow();
            currentStage.close();
            
            // Open view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view.fxml"));
            Parent root = loader.load();
            
            ViewController controller = loader.getController();
            controller.setAdminData(prenom, nom);
            
            Stage mainStage = new Stage();
            Scene scene = new Scene(root);
            mainStage.setTitle("Gestion de Commande - " + prenom + " " + nom);
            mainStage.setScene(scene);
            mainStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la connexion Google: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String nom = userfirstname.getText();
        String prenom = userlastname.getText();
        String email = useremail.getText();
        String username = userusername.getText();
        String telephone = usertelephone.getText();
        String mdp = usermdp.getText();

        // Vérification
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()
                || username.isEmpty() || telephone.isEmpty() || mdp.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs !");
            return;
        }

        // Créer l'objet Admin
        Admin admin = new Admin(nom, prenom, email, username, telephone, mdp);

        // Envoyer au backend
        try {
            // Convertir en JSON
            Gson gson = new Gson();
            String jsonData = gson.toJson(admin);

            // Créer la connexion HTTP
            URL url = new URL("http://localhost:8082/api/admins/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Envoyer les données
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                System.out.println("Admin enregistré avec succès !");

                // Fermer la fenêtre de login
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();

                // Ouvrir view.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view.fxml"));
                Parent root = loader.load();

                ViewController controller = loader.getController();
                controller.setAdminData(prenom, nom);

                Stage mainStage = new Stage();
                Scene scene = new Scene(root);
                mainStage.setTitle("Gestion de Commande - " + prenom + " " + nom);
                mainStage.setScene(scene);
                mainStage.show();
            } else {
                System.out.println("Erreur lors de l'enregistrement !");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }
}
