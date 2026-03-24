package be.doebi.aerismill.ui;

import be.doebi.aerismill.machine.MachineStatus;
import be.doebi.aerismill.machine.connection.BaudRate;
import be.doebi.aerismill.service.DroPollingService;
import be.doebi.aerismill.service.UIStateService;
import be.doebi.aerismill.service.MachineControlService;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MachineController {

    private static final int MIN_RPM = 100;
    private static final int MAX_RPM = 24000;
    private static final int RPM_STEP = 50;

    private boolean spindleRunning = false;
    private boolean spindleSpeedDirty = false;

    private final MachineControlService machineControlService = new MachineControlService();
    private final DroPollingService droPollingService = new DroPollingService(machineControlService);
    private Consumer<MachineStatus> statusListener;

    @FXML
    private BorderPane machineRootPane;

    @FXML
    private ComboBox<String> portComboBox;

    @FXML
    private ComboBox<BaudRate> baudRateComboBox;

    @FXML private Label xMachineLabel;
    @FXML private Label xWorkLabel;
    @FXML private Label yMachineLabel;
    @FXML private Label yWorkLabel;
    @FXML private Label zMachineLabel;
    @FXML private Label zWorkLabel;

    @FXML
    private Label machineStatusLabel;

    @FXML
    private TextField spindleSpeedField;

    @FXML
    private Button homeMachineButton;

    @FXML
    public void initialize() {
        refreshAvailablePorts();
        addBaudRates();
        setupSpindleSpeedField();

        homeMachineButton.setDisable(true);
        setStatusListener(statusListener);
        machineControlService.setStatusListener(this::updateDro);

        Platform.runLater(() -> {
            restoreUiState();
        });
    }

    public void setStatusListener(Consumer<MachineStatus> statusListener) {
        this.statusListener = statusListener;
    };



    public MachineControlService getMachineControlService() {
        return machineControlService;
    }

    public DroPollingService getDroPollingService() {
        return droPollingService;
    }

    public void shutdownMachineConnection() {
        try {
            if (machineControlService != null && machineControlService.isConnected()) {
                AppConsole.log("[Machine] Closing serial connection before exit...");
                droPollingService.stopDroPolling();
                machineControlService.disconnect();
                machineStatusLabel.setText("Disconnected");
            }
        } catch (Exception e) {
            AppConsole.log("[Machine] Error while closing serial connection on exit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveUiState() {
        UIStateService.getInstance().saveLayoutState(machineRootPane);
    }

    public void restoreUiState() {
        UIStateService.getInstance().restoreLayoutState(machineRootPane);
    }

    @FXML
    public void onRefreshPorts() {
        refreshAvailablePorts();
    }

    @FXML
    public void onConnectMachine() {
        String selectedPort = portComboBox.getValue();
        BaudRate selectedBaud = baudRateComboBox.getValue();

        if (!validateConnectionInput(selectedPort, selectedBaud)) {
            return;
        }

        try {
            int baudRate = parseBaudRate(selectedBaud);
            boolean connected = machineControlService.connect(selectedPort, baudRate);

            if (connected) {
                handleSuccessfulConnection(selectedPort, baudRate);
            } else {
                handleFailedConnection(selectedPort);
            }
        } catch (Exception e) {
            handleConnectionError(e);
        }
    }

    private boolean validateConnectionInput(String selectedPort, BaudRate selectedBaud) {
        if (selectedPort == null || selectedPort.isBlank()) {
            showConnectionProblem("No port selected", "[Machine] Connect clicked, but no port selected.");
            return false;
        }

        if (selectedBaud == null) {
            showConnectionProblem("No baud rate selected", "[Machine] Connect clicked, but no baud rate selected.");
            return false;
        }

        return true;
    }

    private int parseBaudRate(BaudRate selectedBaud) {
        return selectedBaud.getValue();
    }

    private void handleSuccessfulConnection(String selectedPort, int baudRate) {
        homeMachineButton.setDisable(false);
        machineStatusLabel.setText("Connected: " + selectedPort);
        droPollingService.startDroPolling();
        AppConsole.log("[Machine] Connected to " + selectedPort + " @ " + baudRate);
    }

    private void handleFailedConnection(String selectedPort) {
        setDisconnectedUiState("Connection failed");
        AppConsole.log("[Machine] Failed to connect to " + selectedPort);
    }

    private void handleConnectionError(Exception e) {
        setDisconnectedUiState("Connect error");
        AppConsole.log("[Machine] Connect error: " + e.getMessage());
        e.printStackTrace();
    }

    private void showConnectionProblem(String statusText, String logMessage) {
        setDisconnectedUiState(statusText);
        AppConsole.log(logMessage);
    }

    private void setDisconnectedUiState(String statusText) {
        homeMachineButton.setDisable(true);
        machineStatusLabel.setText(statusText);
        droPollingService.stopDroPolling();
    }

    @FXML
    public void onDisconnectMachine() {
        machineControlService.disconnect();
        spindleRunning = false;
        homeMachineButton.setDisable(true);
        machineStatusLabel.setText("Disconnected");
        AppConsole.log("[Machine] Disconnect clicked.");
    }

    @FXML
    public void onHomeMachine() {
        try {
            machineControlService.sendCommand("onHomeMachine", "$H");
            machineStatusLabel.setText("Homing...");
            AppConsole.log("[Machine] Home machine clicked.");
        } catch (Exception e) {
            machineStatusLabel.setText("Home failed");
            AppConsole.log("[Machine] Home machine error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onZeroAll() {
        AppConsole.log("[Machine] Zero All clicked.");
    }

    @FXML
    public void onGoXY0() {
        AppConsole.log("[Machine] Go XY0 clicked.");
    }

    @FXML
    public void onGoToPosition() {
        AppConsole.log("[Machine] Go To... clicked.");
    }

    @FXML
    public void onJogXMinus() {
        AppConsole.log("[Machine] X- clicked.");
    }

    @FXML
    public void onJogXPlus() {
        AppConsole.log("[Machine] X+ clicked.");
    }

    @FXML
    public void onJogYPlus() {
        AppConsole.log("[Machine] Y+ clicked.");
    }

    @FXML
    public void onJogYMinus() {
        AppConsole.log("[Machine] Y- clicked.");
    }

    @FXML
    public void onJogZPlus() {
        AppConsole.log("[Machine] Z+ clicked.");
    }

    @FXML
    public void onJogZMinus() {
        AppConsole.log("[Machine] Z- clicked.");
    }

    @FXML
    public void onStopJog() {
        AppConsole.log("[Machine] Stop clicked.");
    }

    @FXML
    public void onPresetRapid() {
        AppConsole.log("[Machine] Rapid preset selected.");
    }

    @FXML
    public void onPresetNormal() {
        AppConsole.log("[Machine] Normal preset selected.");
    }

    @FXML
    public void onPresetPrecise() {
        AppConsole.log("[Machine] Precise preset selected.");
    }

    @FXML
    public void onSpindleStart() {
        try {
            int rpm = getSpindleSpeed();
            machineControlService.spindleStart(rpm);
            spindleRunning = true;
            spindleSpeedField.setText(String.valueOf(rpm));
            machineStatusLabel.setText("Spindle ON (" + rpm + " RPM)");
            AppConsole.log("[Machine] Spindle start clicked. RPM=" + rpm);
        } catch (Exception e) {
            machineStatusLabel.setText("Spindle start failed");
            AppConsole.log("[Machine] Spindle start error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onSpindleStop() {
        try {
            machineControlService.spindleStop();
            spindleRunning = false;
            machineStatusLabel.setText("Spindle OFF");
            AppConsole.log("[Machine] Spindle stop clicked.");
        } catch (Exception e) {
            machineStatusLabel.setText("Spindle stop failed");
            AppConsole.log("[Machine] Spindle stop error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onSpindleSpeedDown() {
        updateSpindleSpeed(-RPM_STEP);
        spindleSpeedDirty = false;
        spindleSpeedField.setStyle("-fx-font-style: normal;");
    }

    @FXML
    public void onSpindleSpeedUp() {
        updateSpindleSpeed(RPM_STEP);
        spindleSpeedDirty = false;
        spindleSpeedField.setStyle("-fx-font-style: normal;");
    }

    @FXML
    public void onSpindleSpeedFieldChange() {
        try {
            updateSpindleSpeed(0);
            spindleSpeedDirty = false;
            spindleSpeedField.setStyle("-fx-font-style: normal;");
            AppConsole.log("[Machine] Spindle speed applied from text field.");
        } catch (Exception e) {
            AppConsole.log("[Machine] Failed to apply spindle speed from text field: " + e.getMessage());
        }
    }

    private void refreshAvailablePorts() {
        AppConsole.log("[Machine] Refreshing serial ports...");

        List<String> portNames = loadAvailablePortNames();
        updatePortComboBox(portNames);
        updatePortRefreshStatus(portNames);
    }

    private List<String> loadAvailablePortNames() {
        SerialPort[] ports = SerialPort.getCommPorts();
        List<String> portNames = new ArrayList<>();

        for (SerialPort port : ports) {
            String portName = port.getSystemPortName();
            portNames.add(portName);
            AppConsole.log("[Machine] Found port: " + portName);
        }

        return portNames;
    }

    private void updatePortComboBox(List<String> portNames) {
        portComboBox.setItems(FXCollections.observableArrayList(portNames));
    }

    private void updatePortRefreshStatus(List<String> portNames) {
        if (portNames.isEmpty()) {
            portComboBox.getSelectionModel().clearSelection();
            machineStatusLabel.setText("No ports found");
            AppConsole.log("[Machine] No serial ports found.");
            return;
        }

        portComboBox.getSelectionModel().selectFirst();
        machineStatusLabel.setText("Ports loaded");
        AppConsole.log("[Machine] Serial ports loaded: " + portNames);
    }

    private void addBaudRates() {
        baudRateComboBox.setItems(FXCollections.observableArrayList(BaudRate.values()));
        baudRateComboBox.setValue(BaudRate.B115200);
    }

    private void setupSpindleSpeedField() {
        spindleSpeedField.textProperty().addListener((obs, oldValue, newValue) -> {

            spindleSpeedDirty = true;
            spindleSpeedField.setStyle("-fx-font-style: italic;");
        });

        spindleSpeedField.setOnAction(event -> onSpindleSpeedFieldChange());
    }



    private int getSpindleSpeed() {
        String text = spindleSpeedField.getText();

        if (text == null || text.isBlank()) {
            return 100;
        }

        int rpm = Integer.parseInt(text.trim());
        return clampRpm(rpm);
    }

    private int clampRpm(int rpm) {
        return Math.max(MIN_RPM, Math.min(MAX_RPM, rpm));
    }

    private void updateSpindleSpeed(int delta) {
        int current = getSpindleSpeed();
        int updated = clampRpm(current + delta);

        spindleSpeedField.setText(String.valueOf(updated));

        if (machineControlService.isConnected()) {
            machineStatusLabel.setText("Spindle RPM (" + updated + " RPM)");
            machineControlService.sendCommand("updateSpindleSpeed", "s" + updated);
            AppConsole.log("[Machine] RPM updated to " + updated);
        } else {
            machineStatusLabel.setText("RPM set to " + updated);
            AppConsole.log("[Machine] RPM set to " + updated);
        }
    }

    private void updateDro(MachineStatus status) {
        System.out.println("inside");
        xMachineLabel.setText(formatAxis(status.getMachineX()));
        yMachineLabel.setText(formatAxis(status.getMachineY()));
        zMachineLabel.setText(formatAxis(status.getMachineZ()));

        // temporary: until WPos parsing exists
        xWorkLabel.setText(formatAxis(status.getMachineX()));
        yWorkLabel.setText(formatAxis(status.getMachineY()));
        zWorkLabel.setText(formatAxis(status.getMachineZ()));
    }

    private String formatAxis(double value) {
        return String.format("%.3f", value);
    }
}