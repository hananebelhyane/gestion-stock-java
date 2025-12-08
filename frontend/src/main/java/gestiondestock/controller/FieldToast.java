package gestiondestock.controller;

import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.geometry.Bounds;

public class FieldToast {
    
    /**
     * Display a tooltip near a text field for validation feedback
     */
    public static void show(TextInputControl field, String message) {
        if (field == null || message == null || message.isEmpty()) {
            return;
        }
        
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        
        // Position tooltip near the field
        field.setTooltip(tooltip);
        
        // Auto-hide after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    if (field.getTooltip() == tooltip) {
                        field.setTooltip(null);
                    }
                });
            } catch (InterruptedException e) {
                // ignore
            }
        }).start();
    }
}
