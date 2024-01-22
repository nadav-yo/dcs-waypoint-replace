package org.faulty.wpreplace.utils;

import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class MessageUtils {
    public static void showInfo(String title, String message) {
        showMessage(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showError(String title, String message) {
        showMessage(Alert.AlertType.ERROR, title, message);
    }

    private static void showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
