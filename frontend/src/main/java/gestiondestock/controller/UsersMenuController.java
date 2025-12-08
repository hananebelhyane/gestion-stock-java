package gestiondestock.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersMenuController {
    
    private static final Logger LOGGER = Logger.getLogger(UsersMenuController.class.getName());
    
    @FXML private Button btnClients;
    @FXML private Button btnMagasiniers;
    @FXML private Button btnRetour;
    
    @FXML
    public void initialize() {
        // Effets hover pour les boutons
        setupHoverEffects();
    }
    
    private void setupHoverEffects() {
        String normalStyle = "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: 600; -fx-background-radius: 12; -fx-padding: 30 40 30 40; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);";
        String hoverStyle = "-fx-background-color: #2a2a2a; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: 600; -fx-background-radius: 12; -fx-padding: 30 40 30 40; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 6);";
        
        if (btnClients != null) {
            btnClients.setOnMouseEntered(e -> btnClients.setStyle(hoverStyle));
            btnClients.setOnMouseExited(e -> btnClients.setStyle(normalStyle));
        }
        
        if (btnMagasiniers != null) {
            btnMagasiniers.setOnMouseEntered(e -> btnMagasiniers.setStyle(hoverStyle));
            btnMagasiniers.setOnMouseExited(e -> btnMagasiniers.setStyle(normalStyle));
        }
        
        if (btnRetour != null) {
            String retourNormal = "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12 24 12 24; -fx-cursor: hand; -fx-font-size: 14; -fx-font-weight: 600;";
            String retourHover = "-fx-background-color: #f5f5f5; -fx-border-color: #c0c0c0; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12 24 12 24; -fx-cursor: hand; -fx-font-size: 14; -fx-font-weight: 600;";
            
            btnRetour.setOnMouseEntered(e -> btnRetour.setStyle(retourHover));
            btnRetour.setOnMouseExited(e -> btnRetour.setStyle(retourNormal));
        }
    }
    
    @FXML
    private void openClientsView(ActionEvent event) {
        loadViewInMainLayout("client-view");
    }
    
    @FXML
    private void openMagasiniersView(ActionEvent event) {
        loadViewInMainLayout("magasinier-view");
    }
    
    @FXML
    private void returnToDashboard(ActionEvent event) {
        loadViewInMainLayout("dashboard");
    }
    
    /**
     * Charge une vue dans le contentRoot du layoutBar principal
     */
    private void loadViewInMainLayout(String viewName) {
    try {
        // Récupérer la scène actuelle
        Scene currentScene = btnClients.getScene();
        if (currentScene == null) {
            LOGGER.warning("Scene is null");
            return;
        }
        
        // Trouver le contentRoot dans le layoutBar
        Node root = currentScene.getRoot();
        StackPane contentRoot = findContentRoot(root);
        
        if (contentRoot == null) {
            LOGGER.warning("ContentRoot not found in scene");
            return;
        }
        
        // Charger la nouvelle vue
        String fxmlPath = "/fxml/" + viewName + ".fxml";
        Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
        
        // ✅ CORRECTION : Ajouter les CSS avec le bon chemin
        if (viewName.equals("client-view")) {
            currentScene.getStylesheets().clear();
            var baseCSS = getClass().getResource("/css/login.css");
            if (baseCSS != null) {
                currentScene.getStylesheets().add(baseCSS.toExternalForm());
            }
            var clientCSS = getClass().getResource("/css/client-style.css");
            if (clientCSS != null) {
                currentScene.getStylesheets().add(clientCSS.toExternalForm());
            }
        } else if (viewName.equals("magasinier-view")) {
            currentScene.getStylesheets().clear();
            var baseCSS = getClass().getResource("/css/login.css");
            if (baseCSS != null) {
                currentScene.getStylesheets().add(baseCSS.toExternalForm());
            }
            var magCSS = getClass().getResource("/css/magasinier-style.css");
            if (magCSS != null) {
                currentScene.getStylesheets().add(magCSS.toExternalForm());
            }
        } else if (viewName.equals("dashboard")) {
            currentScene.getStylesheets().clear();
            var baseCSS = getClass().getResource("/css/login.css");
            if (baseCSS != null) {
                currentScene.getStylesheets().add(baseCSS.toExternalForm());
            }
            var dashCSS = getClass().getResource("/css/dashboard.css");
            if (dashCSS != null) {
                currentScene.getStylesheets().add(dashCSS.toExternalForm());
            }
        }
        
        // Remplacer le contenu
        contentRoot.getChildren().setAll(view);
        
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue: " + viewName, e);
        e.printStackTrace(); // ✅ Ajoutez cette ligne pour voir l'erreur complète
    }
}
    
    /**
     * Recherche récursive du StackPane contentRoot dans l'arbre de nodes
     */
    private StackPane findContentRoot(Node node) {
        if (node instanceof StackPane && node.getId() != null && node.getId().equals("contentRoot")) {
            return (StackPane) node;
        }
        
        if (node instanceof javafx.scene.Parent) {
            for (Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                StackPane result = findContentRoot(child);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
}