package gestiondestock.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.util.Duration;

public class FieldToast {
    public static void show(Node owner, String message) {
        if (owner == null || message == null || message.isBlank()) return;

        Label label = new Label(message);
        label.getStyleClass().add("field-toast");
        label.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.getContent().add(label);

        Bounds b = owner.localToScreen(owner.getBoundsInLocal());
        if (b == null) return;

        double x = b.getMinX();
        double y = b.getMaxY() + 6; // just below the field
        popup.show(owner, x, y);

        label.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), label);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition hold = new PauseTransition(Duration.seconds(2.0));
        hold.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), label);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> popup.hide());
            fadeOut.play();
        });
        hold.play();
    }
}