package gestiondestock.controller;

import gestiondestock.model.Session;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ViewController {
    @FXML private Label WelcomeLabel;
    @FXML private VBox sidebar;
    @FXML private BorderPane root;
    @FXML private ImageView logoImage;
    @FXML private javafx.scene.layout.HBox topbar;
    @FXML private Button sidebarToggleBtn;
    @FXML private Label avatarLabel;
    @FXML private Label usernameLabel;
    @FXML private javafx.scene.control.Button profileMenuBtn;
    @FXML private StackPane contentRoot;

    @FXML
    public void initialize() {
        var s = Session.get();
        if (s.getUsername() != null && s.getRole() != null) {
            if (s.getRole().equalsIgnoreCase("MAGASINIER")) {
                WelcomeLabel.setText("Bienvenue Magasinier: \"" + s.getUsername() + "\"");
            } else if (s.getRole().equalsIgnoreCase("CLIENT")) {
                WelcomeLabel.setText("Bienvenue Client: \"" + s.getUsername() + "\"");
            } else {
                WelcomeLabel.setText("Vous êtes: \"" + s.getUsername() + "\" (" + s.getRole() + ") , welcome");
            }
        }
        // Show sidebar only for admin role if provided
        String role = s.getRole();
        boolean isAdmin = role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN") || role.contains("ADMIN"));
        if (sidebar != null) { sidebar.setVisible(isAdmin); sidebar.setManaged(isAdmin); }
        if (topbar != null) { topbar.setVisible(isAdmin); topbar.setManaged(isAdmin); }

        // Load logo image from provided assets, fallback safe
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
                        break;
                    }
                } catch (Exception ignore) { /* try next */ }
            }
            if (!set) {
                logoImage.setVisible(false);
                logoImage.setManaged(false);
            }
        }

        // Init sidebar toggle state (in topbar, right side)
        if (sidebarToggleBtn != null) {
            sidebarToggleBtn.setText("⟨");
        }

        // Profile card: set avatar letter and username
        String uname = s.getUsername() != null ? s.getUsername() : "User";
        if (avatarLabel != null && !uname.isEmpty()) {
            avatarLabel.setText(uname.substring(0, 1).toUpperCase());
        }
        if (usernameLabel != null) {
            usernameLabel.setText(uname);
        }

        // Load dashboard by default for admins
        if (isAdmin) {
            loadContent("dashboard");
        }
    }

    private void loadContent(String name) {
        if (contentRoot == null) return;
        String path = "/fxml/" + name + ".fxml";
        try {
            Parent view = FXMLLoader.load(getClass().getResource(path));
            contentRoot.getChildren().setAll(view);
        } catch (Exception ex) {
            Label fallback = new Label("Failed to load " + name + ": " + ex.getMessage());
            contentRoot.getChildren().setAll(fallback);
        }
    }

    // Sidebar navigation handlers
    @FXML public void openDashboard(ActionEvent e) { loadContent("dashboard"); }
    @FXML public void openProducts(ActionEvent e) { loadContent("products"); }
    //had ster dyal mouad 3endakum chi wahed y9isso
    @FXML public void openOrders(ActionEvent e) { loadContent("GestionDeCommande"); }
    @FXML public void openGestionDeCommande(ActionEvent e) { loadContent("GestionDeCommande"); }
    //hani kan3lemkum
    @FXML public void openClients(ActionEvent e) { loadContent("clients"); }
    //gestion des fournisseurs
    @FXML public void openSuppliers(ActionEvent e) { loadContent("fournisseur-view"); }
    @FXML public void openStock(ActionEvent e) { loadContent("stock"); }
    @FXML public void openAlerts(ActionEvent e) { loadContent("alerts"); }
    //profile admin
    @FXML public void openSettings(ActionEvent e) { loadContent("admin-profile-view"); 
    
    }
    @FXML
    public void toggleSidebar(ActionEvent e) {
        if (sidebar == null) return;
        boolean willShow = !sidebar.isVisible();
        sidebar.setVisible(willShow);
        sidebar.setManaged(willShow);
        if (sidebarToggleBtn != null) sidebarToggleBtn.setText(willShow ? "⟨" : "☰");
    }

    @FXML
    public void showProfileMenu(ActionEvent e) {
        if (profileMenuBtn == null) return;
        ContextMenu menu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit profile");
        setMenuIcon(edit, "/assets/edit.png", "✎");
        edit.setOnAction(ev -> {
            // Placeholder: could open a profile dialog later
        });
        MenuItem signout = new MenuItem("Sign out");
        setMenuIcon(signout, "/assets/signout.png", "⎋");
        signout.setOnAction(ev -> doSignOut());
        menu.getItems().addAll(edit, signout);
        menu.show(profileMenuBtn, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void setMenuIcon(MenuItem item, String path, String fallbackEmoji) {
        try {
            var url = getClass().getResource(path);
            if (url != null) {
                Image img = new Image(url.toExternalForm(), 14, 14, true, true);
                item.setGraphic(new ImageView(img));
                return;
            }
        } catch (Exception ignore) {}
        Label emoji = new Label(fallbackEmoji);
        item.setGraphic(emoji);
    }

    @FXML
    private void doSignOut() {
        try {
            Session.get().clear();
            var stage = (javafx.stage.Stage) root.getScene().getWindow();
            Parent rootNode = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(rootNode);
            var css = getClass().getResource("/css/login.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
        } catch (Exception ex) {
            // ignore navigation errors here
        }
    }
}