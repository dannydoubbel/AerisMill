package be.doebi.aerismill.ui;

import be.doebi.aerismill.io.step.StepReader;
import be.doebi.aerismill.service.StepImportService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MainController {
    @FXML
    private BorderPane rootPane;

    private static final String PREF_LAST_STEP_DIR = "lastStepDirectory";
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    private final StepImportService stepImportService = new StepImportService();




    @FXML
    private void onOpenStepFile(ActionEvent event) {
        System.out.println("Open STEP clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open STEP File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("STEP Files", "*.step", "*.stp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        String lastDirPath = prefs.get(PREF_LAST_STEP_DIR, null);
        if (lastDirPath != null) {
            File lastDir = new File(lastDirPath);
            if (lastDir.exists() && lastDir.isDirectory()) {
                fileChooser.setInitialDirectory(lastDir);
            }
        }

        Stage stage = (Stage) rootPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            File parentDir = selectedFile.getParentFile();
            if (parentDir != null && parentDir.exists()) {
                prefs.put(PREF_LAST_STEP_DIR, parentDir.getAbsolutePath());
            }
            stepImportService.open(selectedFile);
        }
    }

    private void openStepFile(File file) {
        System.out.println("Opening STEP file: " + file.getAbsolutePath());

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("AerisMill - " + file.getName());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("STEP Open");
        alert.setHeaderText("Selected STEP file");
        alert.setContentText(file.getAbsolutePath());
        alert.showAndWait();

        // TODO: real STEP loading logic
    }

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
