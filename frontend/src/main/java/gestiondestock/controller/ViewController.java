package gestiondestock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ViewController {

    @FXML
    private Label WelcomeLabel;

    public void setAdminData(String firstname, String lastname) {
        WelcomeLabel.setText("Vous êtes : " + firstname + " " + lastname + ", Welcome to your app");
    }
}
