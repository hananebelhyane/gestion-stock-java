package gestiondestock.controller;

import gestiondestock.model.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ViewController {
    @FXML private Label WelcomeLabel;

    @FXML
    public void initialize() {
        var s = Session.get();
        if (s.getUsername() != null && s.getRole() != null) {
            WelcomeLabel.setText("Vous êtes: \"" + s.getUsername() + "\" (" + s.getRole() + ") , welcome");
        }
    }
}
