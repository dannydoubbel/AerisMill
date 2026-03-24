package be.doebi.aerismill.ui;

import be.doebi.aerismill.service.StepImportService;
import be.doebi.aerismill.service.UIStateService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MainController {

    private static final String PREF_LAST_STEP_DIR = "lastStepDirectory";

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    private final StepImportService stepImportService = new StepImportService();

    private File currentFile;
    private Object currentStepFile; // temporary, until your real model type exists
    private boolean manualSerialSendDirty = false;

    @FXML
    private MachineController machinePaneController;

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private TextArea comConsoleOutput;

    @FXML
    private TextField infoField;

    @FXML
    private TextField textManualSerialSend;


    @FXML
    public void initialize() {
        AppConsole.setConsoleConsumer(message -> {
            consoleOutput.appendText(message + System.lineSeparator());
        });

        setupManualSerialSendField();

        Platform.runLater(() -> {
            UIStateService.getInstance().restoreLayoutState(rootPane);
        });
    }

    public void appendComConsoleLine(String line) {
        comConsoleOutput.appendText(line + System.lineSeparator());
    }

    @FXML
    public void onManualSerialSend() {
        String command = textManualSerialSend.getText();

        if (command == null || command.isBlank()) {
            return;
        }

        appendComConsoleLine("TX >>> " + command);

        machinePaneController
                .getMachineControlService()
                .sendCommand("onManualSerialSend", command);

        manualSerialSendDirty = false;
        textManualSerialSend.setStyle("-fx-font-style: normal;");
        textManualSerialSend.setText("");
    }

    @FXML
    private void onOpenStepFile(ActionEvent event) {
        if (hasLoadedFile()) {
            showWarning("File already loaded", "Please close the current file first.");
            return;
        }

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

            try {
                Object loadedModel = stepImportService.open(selectedFile);

                currentFile = selectedFile;
                currentStepFile = loadedModel;

                stage.setTitle("AerisMill - " + selectedFile.getName());

                setInfoPath(selectedFile.getAbsolutePath());
                log(selectedFile.getName() + " loaded successfully.");

                System.out.println(loadedModel);
            } catch (Exception e) {
                log("Failed to load " + selectedFile.getName());
                showWarning("Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onCloseFile(ActionEvent event) {
        if (!hasLoadedFile()) {
            showInfo("No file loaded", "There is no file to close.");
            return;
        }

        boolean confirmed = showConfirm("Close file", "Are you sure you want to close the current file?");
        if (!confirmed) {
            return;
        }

        String closedPath = currentFile.getAbsolutePath();

        currentFile = null;
        currentStepFile = null;

        clearInfoPath();
        log("Closed file: " + closedPath);

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("AerisMill");
    }

    @FXML
    private void onExitApplication(ActionEvent event) {
        System.out.println("Saving rootPane");
        UIStateService.getInstance().saveLayoutState(rootPane);
        System.out.println("Rootpane saved");

        System.out.println("Machinecontroller is null : " + (machinePaneController == null));
        System.out.println("Saving machinePane");

        if (machinePaneController != null) {
            machinePaneController.saveUiState();
        }

        System.out.println("Saving done");

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
            if (machinePaneController != null) {
                machinePaneController.shutdownMachineConnection();
            }

            Platform.exit();
        }
    }

    private void setupManualSerialSendField() {
        textManualSerialSend.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                manualSerialSendDirty = false;
                textManualSerialSend.setStyle("-fx-font-style: normal;");
            } else {
                manualSerialSendDirty = true;
                textManualSerialSend.setStyle("-fx-font-style: italic;");
            }
        });

        textManualSerialSend.setOnAction(event -> onManualSerialSend());
    }

    private boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        addAppCss(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private boolean hasLoadedFile() {
        return currentFile != null;
    }

    private void log(String message) {
        if (consoleOutput != null) {
            consoleOutput.appendText(message + System.lineSeparator());
        } else {
            System.out.println(message);
        }
    }

    private void setInfoPath(String path) {
        if (infoField != null) {
            infoField.setText(path);
        }
    }

    private void clearInfoPath() {
        if (infoField != null) {
            infoField.setText("");
        }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        addAppCss(alert);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        addAppCss(alert);
        alert.showAndWait();
    }

    private void addAppCss(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/be/doebi/aerismill/ui/app.css").toExternalForm()
        );
    }
}