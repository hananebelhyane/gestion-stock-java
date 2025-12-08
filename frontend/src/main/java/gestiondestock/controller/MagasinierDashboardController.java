package gestiondestock.controller;

import gestiondestock.service.MagasinierDashboardService;
import gestiondestock.model.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MagasinierDashboardController {
    
    @FXML private ImageView logoImage;
    @FXML private BorderPane root;
    @FXML private StackPane contentRoot;
    @FXML private VBox sidebar;
    @FXML private Button sidebarToggleBtn;

    @FXML
    public void initialize() {
        // Charger le logo
        loadLogo();
        
        // Initialiser le bouton toggle
        if (sidebarToggleBtn != null) {
            sidebarToggleBtn.setText("⟨");
        }
        
        // Charger le dashboard principal au démarrage
        System.out.println("📄 Chargement initial du dashboard magasinier");
        loadContent("magasinier_dashboard_content");
    }

    /**
     * Charge le logo dans la sidebar (même méthode que ViewController)
     */
    private void loadLogo() {
        if (logoImage != null) {
            String[] candidates = new String[] {
                    "/assets/gs.png",
            };
            boolean set = false;
            for (String path : candidates) {
                try {
                    var url = getClass().getResource(path);
                    if (url != null) {
                        logoImage.setImage(new Image(url.toExternalForm(), true));
                        set = true;
                        System.out.println("✅ Logo chargé avec succès depuis: " + path);
                        break;
                    }
                } catch (Exception ignore) { /* try next */ }
            }
            if (!set) {
                logoImage.setVisible(false);
                logoImage.setManaged(false);
                System.err.println("⚠️ Aucun logo trouvé dans les chemins candidats");
            }
        }
    }

    // ==================== MÉTHODES DE NAVIGATION ====================
    
    @FXML
    public void navigateToMainPage() {
        System.out.println("✅ Navigation vers Main Page Dashboard");
        loadContent("magasinier_dashboard_content");
    }

    @FXML
    public void navigateToGestionProduits() {
        System.out.println("📦 Navigation vers Gestion des Produits");
        loadContent("produit");
    }

    @FXML
    public void navigateToGestionStock() {
        System.out.println("📋 Navigation vers Gestion du Stock");
        loadContent("gestion_stock_magasinier");
    }

    @FXML
    public void navigateToRecevoirAlertes() {
        System.out.println("🔔 Navigation vers Recevoir des Alertes");
        loadContent("alerts_view");
    }

    @FXML
    public void navigateToConsulterCommandes() {
        System.out.println("🛒 Navigation vers Gestion des Commandes");
        loadContent("GestionDeCommande");
    }

    @FXML
    public void navigateToProfilMagasinier() {
        System.out.println("👤 Navigation vers Profil Magasinier");
        loadContent("magasinier-profile-view");
    }

    /**
     * Charge un module FXML dans la zone de contenu (comme ViewController)
     */
    private void loadContent(String name) {
        if (contentRoot == null) {
            System.err.println("❌ contentRoot est null");
            return;
        }
        
        String path = "/fxml/" + name + ".fxml";
        try {
            System.out.println("📄 Chargement du module: " + path);
            
            Parent view = FXMLLoader.load(getClass().getResource(path));
            
            // Nettoyer les CSS précédents
            Scene scene = contentRoot.getScene();
            if (scene != null) {
                scene.getStylesheets().clear();
                
                // Charger le CSS spécifique selon le module
                String cssPath = null;
                switch (name) {
                    case "gestion_stock_magasinier":
                        cssPath = "/styles/GestionStockMagasinierStyles.css";
                        break;
                    case "alerts_view":
                        cssPath = "/styles/AlertsStyles.css";
                        break;
                    case "magasinier-profile-view":
                        cssPath = "/styles/magasinier-profile-style.css";
                        break;
                    case "produit":
                        // Produit utilise probablement un CSS commun ou inline styles
                        break;
                    case "GestionDeCommande":
                        // GestionDeCommande utilise des styles inline
                        break;
                }
                
                if (cssPath != null) {
                    var css = getClass().getResource(cssPath);
                    if (css != null) {
                        scene.getStylesheets().add(css.toExternalForm());
                        System.out.println("✅ CSS chargé: " + cssPath);
                    }
                }
            }
            
            contentRoot.getChildren().setAll(view);
            System.out.println("✅ Module '" + name + "' chargé avec succès dans contentRoot");
            
        } catch (Exception ex) {
            System.err.println("❌ Erreur lors du chargement du module: " + name);
            ex.printStackTrace();
            
            Label fallback = new Label(
                "❌ Erreur de chargement du module '" + name + "'\n\n" +
                "Erreur: " + ex.getMessage() + "\n\n" +
                "Vérifiez que:\n" +
                "1. Le fichier " + path + " existe\n" +
                "2. Le contrôleur est correctement configuré\n" +
                "3. Tous les imports sont corrects"
            );
            fallback.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626; -fx-padding: 40;");
            fallback.setWrapText(true);
            contentRoot.getChildren().setAll(fallback);
        }
    }

    // ==================== DÉCONNEXION ====================
    
    /**
     * Toggle la visibilité de la sidebar (comme dans ViewController)
     */
    @FXML
    public void toggleSidebar() {
        if (sidebar == null) return;
        boolean willShow = !sidebar.isVisible();
        sidebar.setVisible(willShow);
        sidebar.setManaged(willShow);
        if (sidebarToggleBtn != null) {
            sidebarToggleBtn.setText(willShow ? "⟨" : "☰");
        }
        System.out.println("🔄 Sidebar " + (willShow ? "affichée" : "masquée"));
    }
    
    @FXML
    public void handleLogout() {
        System.out.println("🚪 Tentative de déconnexion");
        
        // Demander confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous serez redirigé vers la page de connexion.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                logout();
            }
        });
    }

    /**
     * Effectue la déconnexion et retourne à la page de login
     */
    private void logout() {
        try {
            // Effacer la session (même méthode que ViewController)
            Session.get().clear();
            
            System.out.println("✅ Session effacée");
            
            // Retourner à la page de login
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Scene scene = new Scene(loginRoot);
            
            // Charger les styles CSS
            try {
                var css = getClass().getResource("/styles/login.css");
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                }
            } catch (Exception e) {
                System.out.println("CSS login.css non trouvé (optionnel)");
            }
            
            stage.setScene(scene);
            stage.setTitle("Connexion - Gestion de Stock");
            
            System.out.println("✅ Déconnexion réussie - Retour à la page de login");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la déconnexion:");
            e.printStackTrace();
            showErrorAlert("Erreur", "Une erreur est survenue lors de la déconnexion: " + e.getMessage());
        }
    }

    // ==================== ALERTES ====================
    
    /**
     * Affiche une alerte d'information
     */
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}