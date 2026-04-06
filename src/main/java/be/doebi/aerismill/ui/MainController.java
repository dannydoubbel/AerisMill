package be.doebi.aerismill.ui;

import be.doebi.aerismill.assemble.step.geom.AssemblyIssue;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.fx.viewer.MeshViewerPane;
import be.doebi.aerismill.io.stl.AsciiStlReader;
import be.doebi.aerismill.io.stl.BinaryStlReader;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshBounds;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.service.StepAssemblyService;
import be.doebi.aerismill.service.StepImportService;
import be.doebi.aerismill.service.UIStateService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MainController {

    private static final String PREF_LAST_OPEN_DIR = "lastStepDirectory";
    private static final String ABOUT_TEXT = """
            AerisMill is a CNC-focused engineering application designed to import STEP geometry, build an internal geometric and topological memory model, and eventually generate machining toolpaths. The project emphasizes clear architecture, validation, and pragmatic engineering evolution. This text is temporary and can be refined later.
            """;

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    private final StepImportService stepImportService = new StepImportService();
    private final StepAssemblyService stepAssemblyService = new StepAssemblyService();

    private final MeshViewerPane meshViewerPane = new MeshViewerPane();
    private final AsciiStlReader asciiStlReader = new AsciiStlReader();
    private final BinaryStlReader binaryStlReader = new BinaryStlReader();

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
    private StackPane mainWorkArea;


    @FXML
    public void initialize() {
        AppConsole.setConsoleConsumer(message -> {
            consoleOutput.appendText(message + System.lineSeparator());
        });
        mainWorkArea.getChildren().add(meshViewerPane);


        setupManualSerialSendField();

        Platform.runLater(() -> {
            UIStateService.getInstance().restoreLayoutState(rootPane);
            installCloseHandler();
        });
    }

    private void installCloseHandler() {
        getStage().setOnCloseRequest(event -> {
            event.consume();
            handleExit();
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
        if (!ensureReadyToOpenFile()) {
            return;
        }

        File selectedFile = chooseStepFile();
        if (selectedFile == null) {
            return;
        }

        rememberLastDirectory(selectedFile);

        try {
            StepModel loadedModel = stepImportService.open(selectedFile);

            AssemblyResult assemblyResult = stepAssemblyService.assemble(loadedModel);

            applyLoadedStepFile(selectedFile, loadedModel);
            applyAssemblyResult(assemblyResult);

        } catch (Exception e) {
            handleOpenStepFileFailure(selectedFile, e);
        }
    }

    private boolean ensureReadyToOpenFile() {
        return !hasLoadedFile() || confirmCloseCurrentFileAndContinueOpening();
    }

    private boolean confirmCloseCurrentFileAndContinueOpening() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType closeAndOpenButton = new ButtonType("Close current file and open another", ButtonBar.ButtonData.OK_DONE);

        alert.setTitle("File already open");
        alert.setHeaderText(null);
        alert.setContentText("A file is already open. It must be closed before another file can be opened.");
        alert.getButtonTypes().setAll(cancelButton, closeAndOpenButton);
        addAppCss(alert.getDialogPane());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != closeAndOpenButton) {
            return false;
        }

        return closeCurrentFile();
    }

    private File chooseStepFile() {
        FileChooser fileChooser = createStepFileChooser();
        Stage stage = getStage();
        return fileChooser.showOpenDialog(stage);
    }

    private FileChooser createStepFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open STEP File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("STEP Files", "*.step", "*.stp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        applyLastUsedDirectory(fileChooser);
        return fileChooser;
    }

    @FXML
    private void onOpenAsciiStlFile(ActionEvent event) {
        if (!ensureReadyToOpenFile()) {
            return;
        }

        File selectedFile = chooseAsciiStlFile();
        if (selectedFile == null) {
            return;
        }

        rememberLastDirectory(selectedFile);

        try {
            Mesh mesh = asciiStlReader.read(selectedFile.toPath());
            meshViewerPane.setMesh(mesh);
            applyLoadedAsciiStlFile(selectedFile, mesh);

        } catch (Exception ex) {
            handleOpenAsciiStlFileFailure(selectedFile, ex);
        }
    }

    private void applyLoadedAsciiStlFile(File selectedFile, Mesh mesh) {
        currentFile = selectedFile;
        currentStepFile = null;

        MeshBounds bounds = mesh.bounds();

        getStage().setTitle("AerisMill - " + selectedFile.getName());
        infoField.setText(
                selectedFile.getAbsolutePath()
                        + "   |   v: " + mesh.vertexCount()
                        + "   |   t: " + mesh.triangleCount()
                        + "   |   size: "
                        + bounds.sizeX() + " x "
                        + bounds.sizeY() + " x "
                        + bounds.sizeZ()
        );

        log(selectedFile.getName() + " loaded successfully.");
        log("ASCII STL vertices: " + mesh.vertexCount());
        log("ASCII STL triangles: " + mesh.triangleCount());
    }

    private void handleOpenAsciiStlFileFailure(File selectedFile, Exception ex) {
        log("Failed to load " + selectedFile.getName());
        showWarning("Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
        ex.printStackTrace();
    }

    private File chooseAsciiStlFile() {
        FileChooser fileChooser = createAsciiStlFileChooser();
        return fileChooser.showOpenDialog(getStage());
    }

    private FileChooser createAsciiStlFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open ASCII STL");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ASCII STL Files", "*.ast"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        applyLastUsedDirectory(fileChooser);
        return fileChooser;
    }

    private File chooseBinaryStlFile() {
        FileChooser fileChooser = createBinaryStlFileChooser();
        return fileChooser.showOpenDialog(getStage());
    }

    private FileChooser createBinaryStlFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Binary STL");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Binary STL Files", "*.stl"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        applyLastUsedDirectory(fileChooser);
        return fileChooser;
    }

    @FXML
    private void onOpenBinaryStlFile(ActionEvent event) {
        if (!ensureReadyToOpenFile()) {
            return;
        }

        File selectedFile = chooseBinaryStlFile();
        if (selectedFile == null) {
            return;
        }

        rememberLastDirectory(selectedFile);

        try {
            Mesh mesh = binaryStlReader.read(selectedFile.toPath());
            meshViewerPane.setMesh(mesh);
            applyLoadedBinaryStlFile(selectedFile, mesh);

        } catch (Exception ex) {
            handleOpenBinaryStlFileFailure(selectedFile, ex);
        }
    }

    private void applyLoadedBinaryStlFile(File selectedFile, Mesh mesh) {
        currentFile = selectedFile;
        currentStepFile = null;

        MeshBounds bounds = mesh.bounds();

        getStage().setTitle("AerisMill - " + selectedFile.getName());
        infoField.setText(
                selectedFile.getAbsolutePath()
                        + "   |   v: " + mesh.vertexCount()
                        + "   |   t: " + mesh.triangleCount()
                        + "   |   size: "
                        + bounds.sizeX() + " x "
                        + bounds.sizeY() + " x "
                        + bounds.sizeZ()
        );

        log(selectedFile.getName() + " loaded successfully.");
        log("Binary STL vertices: " + mesh.vertexCount());
        log("Binary STL triangles: " + mesh.triangleCount());
    }

    private void handleOpenBinaryStlFileFailure(File selectedFile, Exception ex) {
        log("Failed to load " + selectedFile.getName());
        showWarning("Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
        ex.printStackTrace();
    }




    private void applyLastUsedDirectory(FileChooser fileChooser) {
        String lastDirPath = prefs.get(PREF_LAST_OPEN_DIR, null);
        if (lastDirPath == null) {
            return;
        }

        File lastDir = new File(lastDirPath);
        if (lastDir.exists() && lastDir.isDirectory()) {
            fileChooser.setInitialDirectory(lastDir);
        }
    }

    private void rememberLastDirectory(File selectedFile) {
        File parentDir = selectedFile.getParentFile();
        if (parentDir != null && parentDir.exists()) {
            prefs.put(PREF_LAST_OPEN_DIR, parentDir.getAbsolutePath());
        }
    }



    private void applyLoadedStepFile(File selectedFile, StepModel loadedModel) {
        currentFile = selectedFile;
        currentStepFile = loadedModel;

        getStage().setTitle("AerisMill - " + selectedFile.getName());
        setInfoPath(selectedFile.getAbsolutePath());
        log(selectedFile.getName() + " loaded successfully.");
        log("STEP entities loaded: " + loadedModel.getEntityCount());

        System.out.println(loadedModel);
    }

    private void handleOpenStepFileFailure(File selectedFile, Exception e) {
        log("Failed to load " + selectedFile.getName());
        showWarning("Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
        e.printStackTrace();
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
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

        closeCurrentFile();

    }
    @FXML
    private void onExitApplication(ActionEvent event) {
        handleExit();
    }

    @FXML
    private void onShowAbout(ActionEvent event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(getStage());
        dialog.setTitle("About AerisMill");
        dialog.setHeaderText("AerisMill");

        TextArea aboutTextArea = new TextArea(ABOUT_TEXT);
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

    private void handleExit(){
        saveApplicationState();

        if (!confirmExit()) {
            return;
        }

        shutdownMachineServices();
        Platform.exit();
    }

    private void saveApplicationState() {

        UIStateService.getInstance().saveLayoutState(rootPane);
        UIStateService.getInstance().saveWindowState(getStage());

        if (machinePaneController != null) {
            machinePaneController.saveUiState();
        }
    }

    private boolean confirmExit() {
        Alert alert = createExitConfirmationAlert();
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private Alert createExitConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Really wanna quit?");
        alert.setContentText("Unsaved work may be lost.");

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().
                        getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );

        return alert;
    }

    private void shutdownMachineServices() {
        if (machinePaneController == null) {
            return;
        }

        machinePaneController.shutdownMachineConnection();

        try {
            machinePaneController.getDroPollingService().stopDroPolling();
        } catch (Exception ignored) {
        }
    }

    private void setupManualSerialSendField() {
        textManualSerialSend.textProperty().
                addListener((obs, oldValue, newValue) -> {
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
        addAppCss(alert.getDialogPane());

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private boolean hasLoadedFile() {
        return currentFile != null;
    }

    private boolean closeCurrentFile() {
        if (!hasLoadedFile()) {
            return false;
        }

        String closedPath = currentFile.getAbsolutePath();

        currentFile = null;
        currentStepFile = null;

        meshViewerPane.clear();
        clearInfoPath();
        log("Closed file: " + closedPath);
        getStage().setTitle("AerisMill");

        return true;
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
        addAppCss(alert.getDialogPane());
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        addAppCss(alert.getDialogPane());
        alert.showAndWait();
    }

    private void addAppCss(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/be/doebi/aerismill/ui/app.css")).toExternalForm()
        );
    }

    private void applyAssemblyResult(AssemblyResult assemblyResult) {
        System.out.println("ASSEMBLY RESULT");
        System.out.println("assembled solids: " + assemblyResult.solids().size());
        System.out.println("issues: " + assemblyResult.issues().size());

        for (SolidAssemblyResult solid : assemblyResult.solids()) {
            System.out.println("solid " + solid.stepId()
                    + " | validation errors=" + solid.validationReport().errorCount()
                    + " | warnings=" + solid.validationReport().warningCount());
        }

        for (AssemblyIssue issue : assemblyResult.issues()) {
            System.out.println(issue.severity()
                    + " | " + issue.code()
                    + " | " + issue.stepId()
                    + " | " + issue.message());
        }
    }


}
