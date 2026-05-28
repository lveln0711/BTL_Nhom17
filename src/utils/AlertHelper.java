package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertHelper {

    // Thông báo thông tin
    public static void showInformation(
            String title,
            String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // Thông báo lỗi
    public static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // Thông báo cảnh báo
    public static void showWarning(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // Xác nhận thao tác
    public static boolean showConfirm(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Constructor private chống tạo object
    private AlertHelper() {

    }
}