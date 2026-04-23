package be.doebi.aerismill.ui;

import be.doebi.aerismill.assemble.step.geom.AssemblyIssue;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.fx.viewer.MeshViewerPane;
import be.doebi.aerismill.io.stl.AsciiStlReader;
import be.doebi.aerismill.io.stl.BinaryStlReader;
import be.doebi.aerismill.model.debug.StepLoadTiming;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshBounds;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.service.*;
import be.doebi.aerismill.tessellation.curve.DefaultEdgeDiscretizer;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.face.DefaultFaceTessellator;
import be.doebi.aerismill.tessellation.face.FaceTessellator;
import be.doebi.aerismill.tessellation.polygon.EarClippingPolygonTriangulator;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.projection.DefaultPlaneProjector;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;
import be.doebi.aerismill.tessellation.shell.PreviewShellTessellator;
import be.doebi.aerismill.tessellation.shell.ShellTessellator;
import be.doebi.aerismill.tessellation.solid.DefaultSolidTessellator;
import be.doebi.aerismill.tessellation.solid.SolidTessellator;
import be.doebi.aerismill.ui.dialog.AboutDialog;
import be.doebi.aerismill.ui.dialog.AppDialogs;
import be.doebi.aerismill.ui.dialog.CloseCurrentFileDialog;
import be.doebi.aerismill.ui.dialog.ExitConfirmationDialog;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class MainController {

    private static final String PREF_LAST_OPEN_DIR = "lastStepDirectory";
    private static final int COPY_FEEDBACK_DURATION_MS = 10000;
    private static final String ABOUT_TEXT = """
            AerisMill is a CNC-focused engineering application designed to import STEP geometry, build an internal geometric and topological memory model, and eventually generate machining toolpaths. The project emphasizes clear architecture, validation, and pragmatic engineering evolution. This text is temporary and can be refined later.
            """;

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    private final StepImportService stepImportService = new StepImportService();
    private final StepAssemblyService stepAssemblyService = new StepAssemblyService();





    private final StepAssemblyMeshService stepAssemblyMeshService = createStepAssemblyMeshService();



    private StepAssemblyMeshService createStepAssemblyMeshService() {
        return new DefaultStepAssemblyMeshService(
                new DefaultAssembledSolidMeshService(createSolidTessellator()),
                this::log
        );
    }









    private final MeshViewerPane meshViewerPane = new MeshViewerPane();
    private final AsciiStlReader asciiStlReader = new AsciiStlReader();
    private final BinaryStlReader binaryStlReader = new BinaryStlReader();
    private final Map<Button, PauseTransition> copyFeedbackTimers = new IdentityHashMap<>();
    private final Map<Button, String> copyFeedbackOriginalTexts = new IdentityHashMap<>();

    private File currentFile;
    private Object currentStepFile; // temporary, until your real model type exists
    private boolean manualSerialSendDirty = false;
    private boolean consoleLoggingEnabled = true;

    @FXML
    private MachineController machinePaneController;

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextArea consoleOutput;

    @FXML
    private CheckBox consoleLoggingCheckBox;

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
        configureConsoleLoggingToggle();
        AppConsole.setConsoleConsumer(this::appendConsoleOutput);
        mainWorkArea.getChildren().add(0,meshViewerPane);


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
            long totalStartNanos = System.nanoTime();

            long importStartNanos = System.nanoTime();
            StepModel loadedModel = stepImportService.open(selectedFile);
            long importMillis = (System.nanoTime() - importStartNanos) / 1_000_000L;

            long assemblyStartNanos = System.nanoTime();
            AssemblyResult assemblyResult = stepAssemblyService.assemble(loadedModel);
            long assemblyMillis = (System.nanoTime() - assemblyStartNanos) / 1_000_000L;

            applyLoadedStepFile(selectedFile, loadedModel);
            applyAssemblyResult(assemblyResult);

            try {
                long meshStartNanos = System.nanoTime();
                DebugSurfaceFamilyMeshes debugMeshes =
                        stepAssemblyMeshService.generateDebugSurfaceFamilyMeshes(assemblyResult);
                long meshMillis = (System.nanoTime() - meshStartNanos) / 1_000_000L;

                long totalMillis = (System.nanoTime() - totalStartNanos) / 1_000_000L;

                StepLoadTiming timing = new StepLoadTiming(
                        importMillis,
                        assemblyMillis,
                        meshMillis,
                        totalMillis
                );
                AppConsole.log("\"DEBUG: before applyGeneratedDebugMeshes\"");
                applyGeneratedDebugMeshes(selectedFile, debugMeshes, timing);
                AppConsole.log("DEBUG: after applyGeneratedDebugMeshes");
            } catch (Exception meshException) {
                handlePreviewMeshFailure(selectedFile, meshException);
            }

        } catch (Exception e) {
            handleOpenStepFileFailure(selectedFile, e);
        }
    }

    private boolean ensureReadyToOpenFile() {
        return !hasLoadedFile() || confirmCloseCurrentFileAndContinueOpening();
    }

    private boolean confirmCloseCurrentFileAndContinueOpening() {
        return CloseCurrentFileDialog.confirm(getStage()) && closeCurrentFile();
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
                new FileChooser.ExtensionFilter("STEP Files", "*.step", "*.STEP", "*.stp","*.STP"),
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
        AppDialogs.showWarning(getStage(), "Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
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
        AppDialogs.showWarning(getStage(), "Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
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


    private File getLastUsedDirectory() {
        String path =  prefs.get(PREF_LAST_OPEN_DIR, null);
        if (path == null || path.isBlank()) {
            return null;
        }

        File dir = new File(path);
        return dir.isDirectory() ? dir : null;
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
        AppDialogs.showWarning(getStage(), "Open failed", "Failed to load file:\n" + selectedFile.getAbsolutePath());
        e.printStackTrace();
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @FXML
    private void onCloseFile(ActionEvent event) {
        if (!hasLoadedFile()) {
            AppDialogs.showInfo(getStage(), "No file loaded", "There is no file to close.");
            return;
        }

        boolean confirmed = AppDialogs.showConfirm(getStage(), "Close file", "Are you sure you want to close the current file?");
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
    private void onClearInfoField(ActionEvent event) {
        clearInfoField();
    }

    @FXML
    private void onCopyInfoField(ActionEvent event) {
        copySelectedOrAllText(infoField);
        showCopyFeedback(event);
    }

    @FXML
    private void onClearConsoleOutput(ActionEvent event) {
        clearConsoleOutput();
    }

    @FXML
    private void onCopyConsoleOutput(ActionEvent event) {
        copySelectedOrAllText(consoleOutput);
        showCopyFeedback(event);
    }

    @FXML
    private void onClearComConsole(ActionEvent event) {
        cleanComConsoleOutput();
    }


    @FXML
    private void onCopyComConsole(ActionEvent event) {
        copySelectedOrAllText(comConsoleOutput);
        showCopyFeedback(event);
    }

    private void copySelectedOrAllText(TextInputControl control) {
        if (control == null) {
            return;
        }

        String selectedText = control.getSelectedText();
        String text = selectedText != null && !selectedText.isEmpty()
                ? selectedText
                : control.getText();
        ClipboardContent content = new ClipboardContent();
        content.putString(text == null ? "" : text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    private void showCopyFeedback(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) {
            return;
        }

        copyFeedbackOriginalTexts.putIfAbsent(button, button.getText());

        PauseTransition existingTimer = copyFeedbackTimers.remove(button);
        if (existingTimer != null) {
            existingTimer.stop();
        }

        button.setText("Copied");
        if (!button.getStyleClass().contains("copy-active")) {
            button.getStyleClass().add("copy-active");
        }

        PauseTransition timer = new PauseTransition(Duration.millis(COPY_FEEDBACK_DURATION_MS));
        timer.setOnFinished(finishedEvent -> {
            String originalText = copyFeedbackOriginalTexts.remove(button);
            button.setText(originalText == null ? "Copy" : originalText);
            button.getStyleClass().remove("copy-active");
            copyFeedbackTimers.remove(button);
        });
        copyFeedbackTimers.put(button, timer);
        timer.playFromStart();
    }



    @FXML
    private void onShowAbout(ActionEvent event) {
        AboutDialog.show(getStage(), ABOUT_TEXT);
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
        return ExitConfirmationDialog.confirm(getStage());
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
        appendConsoleOutput(message);
    }

    private void configureConsoleLoggingToggle() {
        if (consoleLoggingCheckBox == null) {
            return;
        }

        consoleLoggingEnabled = consoleLoggingCheckBox.isSelected();
        consoleLoggingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                consoleLoggingEnabled = newValue
        );
    }

    private void appendConsoleOutput(String message) {
        if (!consoleLoggingEnabled) {
            return;
        }

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

    private void clearInfoField() {
        if (infoField != null) {
            infoField.clear();
        }
    }

    private void clearConsoleOutput() {
        if (consoleOutput!=null) {
            consoleOutput.clear();
        }
    }

    private void cleanComConsoleOutput() {
        if (comConsoleOutput!=null) {
            comConsoleOutput.clear();
        }
    }

    private void applyAssemblyResult(AssemblyResult assemblyResult) {
        System.out.println("ASSEMBLY RESULT");
        System.out.println("assembled solids: " + assemblyResult.solids().size());
        System.out.println("issues: " + assemblyResult.issues().size());

        if (assemblyResult.solids().size() > 1) {
            log("Preview note: multiple solids detected; evaluating all solids and combining all previewable solids into one mesh.");
        }

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

    /*
    private void applyGeneratedMesh(File selectedFile, Mesh mesh) {
        meshViewerPane.setMesh(mesh);

        MeshBounds bounds = mesh.bounds();

        infoField.setText(
                selectedFile.getAbsolutePath()
                        + "   |   v: " + mesh.vertexCount()
                        + "   |   t: " + mesh.triangleCount()
                        + "   |   size: "
                        + bounds.sizeX() + " x "
                        + bounds.sizeY() + " x "
                        + bounds.sizeZ()
        );

        log("Mesh generated successfully.");
        log("Mesh vertices: " + mesh.vertices().size());
        log("Mesh triangles: " + mesh.triangles().size());
    }

     */

    private SolidTessellator createSolidTessellator() {
        GeometryTolerance tolerance = GeometryTolerance.defaults();

        EdgeDiscretizer edgeDiscretizer = new DefaultEdgeDiscretizer(/* adjust to your actual ctor */);
        PlaneProjector planeProjector = new DefaultPlaneProjector();

        // Use your real concrete triangulator here.
        // If RecordingPolygonTriangulator is currently your only concrete implementation, it can do for now.
        // PolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        PolygonTriangulator polygonTriangulator = new EarClippingPolygonTriangulator(tolerance);

        FaceTessellator faceTessellator = new DefaultFaceTessellator /* PlanarFaceTessellator*/(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                tolerance
        );

        ShellTessellator shellTessellator = new PreviewShellTessellator(faceTessellator);
        return new DefaultSolidTessellator(shellTessellator);
    }



    private void handlePreviewMeshFailure(File selectedFile, Exception e) {
        log("Preview mesh generation failed for " + selectedFile.getName());
        log("Reason: " + e.getClass().getSimpleName() + " - " + e.getMessage());

        AppDialogs.showWarning(
                getStage(),
                "Preview mesh unavailable",
                "The STEP file was loaded and assembled, but preview mesh generation is not supported for this file yet.\n\n"
                        + "Reason: " + e.getMessage()
        );

        meshViewerPane.clear();
    }

    @FXML
    private void onViewerFit(ActionEvent event) {
        log("fit view");
        meshViewerPane.fitToView();
    }

    @FXML
    private void onViewerReset(ActionEvent event) {
        log("reset view");
        meshViewerPane.resetView();
    }

    @FXML
    private void onViewerZoomIn(ActionEvent event) {
        log("zoom in");
        meshViewerPane.zoomIn();
    }

    @FXML
    private void onViewerZoomOut(ActionEvent event) {
        log("zoom out");
        meshViewerPane.zoomOut();
    }

    private void applyGeneratedDebugMeshes(
            File selectedFile,
            DebugSurfaceFamilyMeshes debugMeshes,
            StepLoadTiming timing
    ) {
        Mesh planar = debugMeshes.planarMesh();
        Mesh cylindrical = debugMeshes.cylindricalMesh();
        Mesh conical = debugMeshes.conicalMesh();

        int vertexCount =
                countVertices(planar) + countVertices(cylindrical) + countVertices(conical);
        int triangleCount =
                countTriangles(planar) + countTriangles(cylindrical) + countTriangles(conical);

        Mesh combinedMesh = firstNonEmpty(planar, cylindrical, conical);
        if (combinedMesh != null) {
            MeshBounds bounds = combinedMesh.bounds();

            infoField.setText(
                    selectedFile.getAbsolutePath()
                            + "   |   v: " + vertexCount
                            + "   |   t: " + triangleCount
                            + "   |   size: "
                            + bounds.sizeX() + " x "
                            + bounds.sizeY() + " x "
                            + bounds.sizeZ()
            );
        } else {
            infoField.setText(selectedFile.getAbsolutePath() + "   |   no debug preview mesh");
        }

        AppConsole.log("DEBUG: before viewer update");
        meshViewerPane.setDebugSurfaceFamilyMeshes(debugMeshes, timing);
        AppConsole.log("DEBUG: after viewer update");

        log("Debug mesh generated successfully.");
        log("Planar triangles: " + countTriangles(planar));
        log("Cylindrical triangles: " + countTriangles(cylindrical));
        log("Conical triangles: " + countTriangles(conical));
    }

    private int countVertices(Mesh mesh) {
        return mesh == null ? 0 : mesh.vertexCount();
    }

    private int countTriangles(Mesh mesh) {
        return mesh == null ? 0 : mesh.triangleCount();
    }

    private Mesh firstNonEmpty(Mesh... meshes) {
        for (Mesh mesh : meshes) {
            if (mesh != null && !mesh.isEmpty()) {
                return mesh;
            }
        }
        return null;
    }


}
