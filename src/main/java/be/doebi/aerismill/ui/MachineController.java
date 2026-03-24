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
    private TextField feedRateField;

    @FXML
    private TextField xyMoveField;

    @FXML
    private TextField zMoveField;

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
            boolean connected = machineControlService.connect(selectedPort,selectedBaud);

            if (connected) {
                handleSuccessfulConnection(selectedPort, selectedBaud);
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



    private void handleSuccessfulConnection(String selectedPort, BaudRate baudRate) {
        homeMachineButton.setDisable(false);
        machineStatusLabel.setText("Connected: " + selectedPort);
        droPollingService.startDroPolling();
        AppConsole.log("[Machine] Connected to " + selectedPort + " @ " + baudRate.getValue());
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
        jog("X", -readDoubleField(xyMoveField, 5.0));
    }

    @FXML
    public void onJogXPlus() {
        jog("X", readDoubleField(xyMoveField, 5.0));
    }

    @FXML
    public void onJogYPlus() {
        jog("Y", readDoubleField(xyMoveField, 5.0));
    }

    @FXML
    public void onJogYMinus() {
        jog("Y", -readDoubleField(xyMoveField, 5.0));
    }

    @FXML
    public void onJogZPlus() {
        jog("Z", readDoubleField(zMoveField, 2.0));
    }

    @FXML
    public void onJogZMinus() {
        jog("Z", -readDoubleField(zMoveField, 2.0));
    }

    @FXML
    public void onStopJog() {
        AppConsole.log("[Machine] Stop clicked.");
        machineControlService.stopJog();
    }

    private void jog(String axis, double distance) {
        int feedRate = readIntField(feedRateField, 3000);

        if (feedRate <= 0) {
            AppConsole.log("[Machine] Feed rate must be > 0");
            return;
        }

        String distanceText = formatDecimal(distance);
        String command = "$J=G91 " + axis + distanceText + " F" + feedRate;

        AppConsole.log("[Machine] Jog command: " + command);
        machineControlService.sendLine(command);
    }

    @FXML
    public void onDecreaseXyMove() {
        adjustDoubleField(xyMoveField, -1.0, 0.1, 1);
    }

    @FXML
    public void onIncreaseXyMove() {
        adjustDoubleField(xyMoveField, 1.0, 0.1, 1);
    }

    @FXML
    public void onDecreaseZMove() {
        adjustDoubleField(zMoveField, -0.5, 0.1, 1);
    }

    @FXML
    public void onIncreaseZMove() {
        adjustDoubleField(zMoveField, 0.5, 0.1, 1);
    }

    @FXML
    public void onDecreaseFeedRate() {
        adjustIntField(feedRateField, -500, 100);
    }

    @FXML
    public void onIncreaseFeedRate() {
        adjustIntField(feedRateField, 500, 100);
    }

    private void adjustDoubleField(TextField field, double delta, double minValue, int decimals) {
        try {
            String text = field.getText().trim().replace(',', '.');
            double current = Double.parseDouble(text);
            double updated = Math.max(minValue, current + delta);
            field.setText(formatDecimal(updated));
        } catch (NumberFormatException e) {
            AppConsole.log("[Machine] Invalid number in field: " + field.getText());
        }
    }

    private void adjustIntField(TextField field, int delta, int minValue) {
        try {
            int current = Integer.parseInt(field.getText().trim());
            int updated = Math.max(minValue, current + delta);
            field.setText(String.valueOf(updated));
        } catch (NumberFormatException e) {
            AppConsole.log("[Machine] Invalid integer in field: " + field.getText());
        }
    }

    @FXML
    public void onPresetRapid() {
        xyMoveField.setText("20.0");
        zMoveField.setText("5.0");
        feedRateField.setText("6000");
        AppConsole.log("[Machine] Rapid preset selected.");
    }

    @FXML
    public void onPresetNormal() {
        xyMoveField.setText("5.0");
        zMoveField.setText("2.0");
        feedRateField.setText("3000");
        AppConsole.log("[Machine] Normal preset selected.");
    }

    @FXML
    public void onPresetPrecise() {
        xyMoveField.setText("0.5");
        zMoveField.setText("0.5");
        feedRateField.setText("800");
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

    private double readDoubleField(TextField field, double fallback) {
        try {
            String text = field.getText().trim().replace(',', '.');
            double value = Double.parseDouble(text);
            field.setText(formatDecimal(value));
            return value;
        } catch (NumberFormatException e) {
            AppConsole.log("[Machine] Invalid number in field: " + field.getText());
            field.setText(formatDecimal(fallback));
            return fallback;
        }
    }

    private int readIntField(TextField field, int fallback) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            AppConsole.log("[Machine] Invalid integer in field: " + field.getText());
            field.setText(String.valueOf(fallback));
            return fallback;
        }
    }

    private String formatDecimal(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }
        return String.format(java.util.Locale.US, "%.3f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }
}