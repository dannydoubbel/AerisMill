package be.doebi.aerismill.ui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

import java.util.Objects;
import java.util.Optional;

public final class AppDialogs {

    private AppDialogs() {
    }

    public static boolean showConfirm(Window owner, String title, String message) {
        Alert alert = createAlert(owner, Alert.AlertType.CONFIRMATION, title, message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void showWarning(Window owner, String title, String message) {
        Alert alert = createAlert(owner, Alert.AlertType.WARNING, title, message);
        alert.showAndWait();
    }

    public static void showInfo(Window owner, String title, String message) {
        Alert alert = createAlert(owner, Alert.AlertType.INFORMATION, title, message);
        alert.showAndWait();
    }

    private static Alert createAlert(Window owner, Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        if (owner != null) {
            alert.initOwner(owner);
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        addAppCss(alert.getDialogPane());

        return alert;
    }

    private static void addAppCss(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(AppDialogs.class.getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );
    }
}
