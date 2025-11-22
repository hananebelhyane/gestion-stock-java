package gestiondestock.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitaire pour la navigation entre les vues
 */
public class NavigationHelper {
    
    private static final Logger LOGGER = Logger.getLogger(NavigationHelper.class.getName());
    
    /**
     * Charge une vue dans le contentRoot du layout principal
     * @param sourceNode N'importe quel node de la scène actuelle
     * @param viewName Nom de la vue à charger (sans .fxml)
     * @param cssPath Chemin optionnel vers un fichier CSS (peut être null)
     */
    public static void loadView(Node sourceNode, String viewName, String cssPath) {
        try {
            Scene currentScene = sourceNode.getScene();
            if (currentScene == null) {
                LOGGER.warning("Scene is null");
                return;
            }
            
            Node root = currentScene.getRoot();
            StackPane contentRoot = findContentRoot(root);
            
            if (contentRoot == null) {
                LOGGER.warning("ContentRoot not found in scene");
                return;
            }
            
            String fxmlPath = "/fxml/" + viewName + ".fxml";
            Parent view = FXMLLoader.load(NavigationHelper.class.getResource(fxmlPath));
            
            // Gérer les CSS
            if (cssPath != null && !cssPath.isEmpty()) {
                currentScene.getStylesheets().clear();
                // Toujours garder le CSS de base
                var baseCSS = NavigationHelper.class.getResource("/styles/login.css");
                if (baseCSS != null) {
                    currentScene.getStylesheets().add(baseCSS.toExternalForm());
                }
                // Ajouter le CSS spécifique
                var specificCSS = NavigationHelper.class.getResource(cssPath);
                if (specificCSS != null) {
                    currentScene.getStylesheets().add(specificCSS.toExternalForm());
                }
            }
            
            contentRoot.getChildren().setAll(view);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la vue: " + viewName, e);
        }
    }
    
    /**
     * Retour au menu utilisateurs
     */
    public static void returnToUsersMenu(Node sourceNode) {
        loadView(sourceNode, "users-menu", null);
    }
    
    /**
     * Retour au dashboard
     */
    public static void returnToDashboard(Node sourceNode) {
        loadView(sourceNode, "dashboard", "/styles/dashboard.css");
    }
    
    /**
     * Navigation vers la gestion des clients
     */
    public static void navigateToClients(Node sourceNode) {
        loadView(sourceNode, "client-view", "/css/client-style.css");
    }
    
    /**
     * Navigation vers la gestion des magasiniers
     */
    public static void navigateToMagasiniers(Node sourceNode) {
        loadView(sourceNode, "magasinier-view", "/css/magasinier-style.css");
    }
    
    /**
     * Recherche récursive du StackPane contentRoot
     */
    private static StackPane findContentRoot(Node node) {
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