package be.doebi.aerismill.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.Optional;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private void onOpenStepFile(ActionEvent event) { }

    @FXML
    private void onCloseFile(ActionEvent event) { }

    @FXML
    private void onExitApplication(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Really wanna quit?");
        alert.setContentText("Unsaved work may be lost.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/be/doebi/aerismill/ui/app.css").toExternalForm()
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            Platform.exit();
        }
    }
}
