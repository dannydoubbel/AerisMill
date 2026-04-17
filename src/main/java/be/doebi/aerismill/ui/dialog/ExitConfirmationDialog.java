package be.doebi.aerismill.ui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Objects;
import java.util.Optional;

public final class ExitConfirmationDialog {

    private ExitConfirmationDialog() {
    }

    public static boolean confirm(Window owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }

        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Really wanna quit?");
        alert.setContentText("Unsaved work may be lost.");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        addAppCss(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private static void addAppCss(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(ExitConfirmationDialog.class.getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );
    }
}
