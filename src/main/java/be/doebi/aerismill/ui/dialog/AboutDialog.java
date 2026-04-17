package be.doebi.aerismill.ui.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.util.Objects;

public final class AboutDialog {

    private AboutDialog() {
    }

    public static void show(Window owner, String aboutText) {
        Dialog<Void> dialog = new Dialog<>();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.setTitle("About AerisMill");
        dialog.setHeaderText("AerisMill");

        TextArea aboutTextArea = new TextArea(aboutText);
        aboutTextArea.setWrapText(true);
        aboutTextArea.setEditable(false);
        aboutTextArea.setPrefColumnCount(48);
        aboutTextArea.setPrefRowCount(10);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(aboutTextArea);
        dialogPane.getButtonTypes().add(ButtonType.OK);
        addAppCss(dialogPane);

        dialog.showAndWait();
    }

    private static void addAppCss(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(AboutDialog.class.getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );
    }
}
