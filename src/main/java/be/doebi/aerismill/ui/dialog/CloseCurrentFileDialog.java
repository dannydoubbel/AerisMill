package be.doebi.aerismill.ui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Objects;
import java.util.Optional;

public final class CloseCurrentFileDialog {

    private CloseCurrentFileDialog() {
    }

    public static boolean confirm(Window owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }

        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType closeAndOpenButton = new ButtonType("Close current file and open another", ButtonBar.ButtonData.OK_DONE);

        alert.setTitle("File already open");
        alert.setHeaderText(null);
        alert.setContentText("A file is already open. It must be closed before another file can be opened.");
        alert.getButtonTypes().setAll(cancelButton, closeAndOpenButton);
        addAppCss(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == closeAndOpenButton;
    }

    private static void addAppCss(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(CloseCurrentFileDialog.class.getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );
    }
}
