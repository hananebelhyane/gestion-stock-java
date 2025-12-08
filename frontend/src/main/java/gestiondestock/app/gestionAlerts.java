package gestiondestock.app;

import gestiondestock.model.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Application standalone pour tester les Notifications et Alertes (Magasinier)
 */
public class gestionAlerts extends Application {

    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Test - Notifications et Alertes");
        afficherEcranLogin();
    }

    private void afficherEcranLogin() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");

        // Titre
        Label titre = new Label("🔔 Notifications & Alertes");
        titre.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label sousTitre = new Label("Test Module Magasinier - Connexion");
        sousTitre.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e;");

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(20);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(450);
        form.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 40; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);"
        );

        Label lblUsername = new Label("👤 Nom d'utilisateur");
        lblUsername.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #2c3e50;");
        
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Ex: magasinier1");
        txtUsername.setPrefWidth(350);
        txtUsername.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 8;"
        );

        Label lblPassword = new Label("🔒 Mot de passe");
        lblPassword.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #2c3e50;");
        
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");
        txtPassword.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 8;"
        );

        Button btnLogin = new Button("Se connecter");
        btnLogin.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #3498db, #2980b9); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 14 40; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        btnLogin.setPrefWidth(350);

        Label lblStatus = new Label();
        lblStatus.setStyle("-fx-font-size: 13px; -fx-font-weight: 600;");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(350);
        lblStatus.setAlignment(Pos.CENTER);

        form.add(lblUsername, 0, 0);
        form.add(txtUsername, 0, 1);
        form.add(lblPassword, 0, 2);
        form.add(txtPassword, 0, 3);
        form.add(btnLogin, 0, 4);
        form.add(lblStatus, 0, 5);

        // Info backend
        Label lblBackend = new Label("🌐 Backend: " + BASE_URL);
        lblBackend.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        Label lblInfo = new Label("⚠️ Assurez-vous que le backend Spring Boot est démarré");
        lblInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c; -fx-font-weight: 600;");

        root.getChildren().addAll(titre, sousTitre, form, lblBackend, lblInfo);

        // Action login
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                lblStatus.setText("⚠️ Veuillez remplir tous les champs");
                lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: 600;");
                return;
            }

            btnLogin.setDisable(true);
            lblStatus.setText("🔄 Connexion en cours...");
            lblStatus.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px; -fx-font-weight: 600;");

            new Thread(() -> {
                try {
                    String token = effectuerLogin(username, password);
                    
                    javafx.application.Platform.runLater(() -> {
                        if (token != null && !token.isEmpty()) {
                            Session.get().setToken(token);
                            lblStatus.setText("✅ Connexion réussie ! Chargement...");
                            lblStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: 600;");
                            
                            new Thread(() -> {
                                try {
                                    Thread.sleep(500);
                                    javafx.application.Platform.runLater(this::chargerInterfaceAlertes);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        } else {
                            lblStatus.setText("❌ Identifiants incorrects");
                            lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: 600;");
                            btnLogin.setDisable(false);
                        }
                    });
                    
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        lblStatus.setText("❌ Erreur: " + ex.getMessage());
                        lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: 600;");
                        btnLogin.setDisable(false);
                        ex.printStackTrace();
                    });
                }
            }).start();
        });

        txtPassword.setOnAction(e -> btnLogin.fire());
        txtUsername.setOnAction(e -> txtPassword.requestFocus());

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private String effectuerLogin(String username, String password) throws Exception {
        String json = String.format(
            "{\"username\":\"%s\",\"password\":\"%s\"}", 
            username, password
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Map<String, Object> result = mapper.readValue(response.body(), Map.class);
            String token = (String) result.get("token");
            
            if (token == null) {
                token = (String) result.get("accessToken");
            }
            if (token == null) {
                token = (String) result.get("jwt");
            }
            
            return token;
        } else {
            throw new Exception("Identifiants incorrects (HTTP " + response.statusCode() + ")");
        }
    }

    private void chargerInterfaceAlertes() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/alerts_view.fxml")
            );
            
            if (loader.getLocation() == null) {
                showError("Erreur", 
                    "Fichier FXML introuvable !\n\n" +
                    "Assurez-vous que le fichier existe :\n" +
                    "src/main/resources/fxml/alerts_view.fxml");
                return;
            }
            
            Parent root = loader.load();

            Scene scene = new Scene(root, 1400, 900);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.setTitle("Notifications et Alertes - Module Magasinier");
            primaryStage.centerOnScreen();

            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("✅ Interface des alertes chargée avec succès !\n\n" +
                                   "Backend: " + BASE_URL);
                alert.show();
                
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> alert.close());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur de chargement", 
                "Impossible de charger l'interface :\n\n" + e.getMessage() +
                "\n\n⚠️ Vérifications à faire :\n" +
                "1. Le fichier FXML existe dans src/main/resources/fxml/\n" +
                "2. Le contrôleur AlertsController existe\n" +
                "3. Tous les imports sont corrects\n" +
                "4. Le fichier CSS existe dans src/main/resources/styles/");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinWidth(600);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}