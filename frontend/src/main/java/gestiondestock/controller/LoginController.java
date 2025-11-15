package gestiondestock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import javafx.scene.Node;
import java.io.IOException;

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
    public void handleLogin(ActionEvent event) {
        // récupérer données (adapter aux noms de vos fields)
        String nom = userfirstname.getText();
        String prenom = userlastname.getText();
        String email = useremail.getText();
        String username = userusername.getText();
        String telephone = usertelephone.getText();
        String mdp = usermdp.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()
                || username.isEmpty() || telephone.isEmpty() || mdp.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs !");
            return;
        }

        try {
            // 1) Appel backend
            Gson gson = new Gson();
            String jsonData = gson.toJson(new gestiondestock.model.Admin(nom, prenom, email, username, telephone, mdp));

            URL url = new URL("http://localhost:8082/api/admins/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            conn.disconnect();

            if (responseCode != 200 && responseCode != 201) {
                System.out.println("Erreur lors de l'enregistrement ! Code: " + responseCode);
                return;
            }

            // 2) Charger view.fxml avant de fermer le login (évite fermeture si load échoue)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view.fxml"));
            Parent root = loader.load();
            gestiondestock.controller.ViewController controller = loader.getController();
            controller.setAdminData(prenom, nom); // transmettez les infos si besoin

            Stage mainStage = new Stage();
            mainStage.setTitle("Gestion - " + prenom + " " + nom);
            mainStage.setScene(new Scene(root));
            mainStage.show();

            // 3) Fermer la fenêtre de login seulement si tout a réussi
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println("Erreur de connexion ou de chargement de la vue : " + e.getMessage());
        }
    }
}
