package be.doebi.aerismill.ui;

import be.doebi.aerismill.service.UIStateService;
import be.doebi.aerismill.service.MachineControlService;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MachineController {

    private static final int MIN_RPM = 100;
    private static final int MAX_RPM = 24000;
    private static final int RPM_STEP = 50;

    private boolean spindleRunning = false;

    @FXML
    private BorderPane machineRootPane;

    @FXML
    private ComboBox<String> portComboBox;

    @FXML
    private ComboBox<String> baudRateComboBox;

    @FXML
    private Label machineStatusLabel;

    @FXML
    private TextField spindleSpeedField;

    @FXML
    public void initialize() {
        refreshAvailablePorts();
        addBaudRates();
        setupSpindleSpeedField();
        Platform.runLater(() -> {
            // uiStateService.restore...
            restoreUiState();
        });

    }

    @FXML
    public void onRefreshPorts() {
        refreshAvailablePorts();
    }



    @FXML
    public void onConnectMachine() {
        String selectedPort = portComboBox.getValue();
        String selectedBaud = baudRateComboBox.getValue();

        if (selectedPort == null || selectedPort.isBlank()) {
            machineStatusLabel.setText("No port selected");
            AppConsole.log("[Machine] Connect clicked, but no port selected.");
            return;
        }

        if (selectedBaud == null || selectedBaud.isBlank()) {
            machineStatusLabel.setText("No baud rate selected");
            AppConsole.log("[Machine] Connect clicked, but no baud rate selected.");
            return;
        }

        try {
            int baudRate = Integer.parseInt(selectedBaud);
            boolean connected = machineControlService.connect(selectedPort, baudRate);

            if (connected) {
                machineStatusLabel.setText("Connected: " + selectedPort);
                AppConsole.log("[Machine] Connected to " + selectedPort + " @ " + baudRate);
            } else {
                machineStatusLabel.setText("Connection failed");
                AppConsole.log("[Machine] Failed to connect to " + selectedPort);
            }
        } catch (Exception e) {
            machineStatusLabel.setText("Connect error");
            AppConsole.log("[Machine] Connect error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onDisconnectMachine() {
        machineControlService.disconnect();
        spindleRunning = false;
        machineStatusLabel.setText("Disconnected");
        AppConsole.log("[Machine] Disconnect clicked.");
    }

    @FXML
    public void onTestS500() {
        try {
            machineControlService.sendCommand("S500");
            AppConsole.log("[Machine] Test S500 sent.");
        } catch (Exception e) {
            AppConsole.log("[Machine] Test S500 failed: " + e.getMessage());
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
        AppConsole.log("[Machine] Spindle speed down clicked.");
    }

    @FXML
    public void onSpindleSpeedUp() {
        updateSpindleSpeed(RPM_STEP);
        AppConsole.log("[Machine] Spindle speed up clicked.");
    }

    private final MachineControlService machineControlService = new MachineControlService();



    private void refreshAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        List<String> portNames = new ArrayList<>();

        AppConsole.log("[Machine] Refreshing serial ports...");

        for (SerialPort port : ports) {
            String portName = port.getSystemPortName();
            portNames.add(portName);
            AppConsole.log("[Machine] Found port: " + portName);
        }

        portComboBox.setItems(FXCollections.observableArrayList(portNames));

        if (!portNames.isEmpty()) {
            portComboBox.getSelectionModel().selectFirst();
            machineStatusLabel.setText("Ports loaded");
            AppConsole.log("[Machine] Serial ports loaded: " + portNames);
        } else {
            portComboBox.getSelectionModel().clearSelection();
            machineStatusLabel.setText("No ports found");
            AppConsole.log("[Machine] No serial ports found.");
        }
    }
    private void addBaudRates() {
        List<String> baudRates = Arrays.asList(
                "9600",
                "19200",
                "38400",
                "57600",
                "115200",
                "230400");
        baudRateComboBox.setItems(FXCollections.observableArrayList(baudRates));
        baudRateComboBox.setValue("115200");
    }

    private void setupSpindleSpeedField() {
        spindleSpeedField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                return;
            }

            String digitsOnly = newValue.replaceAll("[^\\d]", "");
            if (!digitsOnly.equals(newValue)) {
                spindleSpeedField.setText(digitsOnly);
                return;
            }

            try {
                int rpm = Integer.parseInt(digitsOnly);
                int clamped = clampRpm(rpm);

                if (rpm != clamped) {
                    spindleSpeedField.setText(String.valueOf(clamped));
                    return;
                }

                if (spindleRunning && machineControlService.isConnected()) {
                    machineControlService.setSpindleSpeed(clamped);
                    machineStatusLabel.setText("Spindle ON (" + clamped + " RPM)");
                    AppConsole.log("[Machine] RPM changed live to " + clamped);
                }

            } catch (NumberFormatException ignored) {
                // user is still typing, let them breathe
            }
        });

        spindleSpeedField.focusedProperty().addListener((obs, oldFocused, newFocused) -> {
            if (!newFocused) {
                normalizeSpindleSpeedField();
            }
        });

        spindleSpeedField.setOnAction(event -> normalizeSpindleSpeedField());
    }

    private void normalizeSpindleSpeedField() {
        try {
            int rpm = getSpindleSpeed();
            spindleSpeedField.setText(String.valueOf(rpm));

            if (spindleRunning && machineControlService.isConnected()) {
                machineControlService.setSpindleSpeed(rpm);
                machineStatusLabel.setText("Spindle ON (" + rpm + " RPM)");
                AppConsole.log("[Machine] RPM normalized to " + rpm);
            }
        } catch (Exception e) {
            spindleSpeedField.setText("100");
            AppConsole.log("[Machine] Invalid RPM input, reset to 100");
        }
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

        if (spindleRunning && machineControlService.isConnected()) {
            machineStatusLabel.setText("Spindle ON (" + updated + " RPM)");
            AppConsole.log("[Machine] RPM updated to " + updated);
        } else {
            machineStatusLabel.setText("RPM set to " + updated);
            AppConsole.log("[Machine] RPM set to " + updated);
        }
    }

    public void saveUiState() {
        UIStateService.getInstance().saveLayoutState(machineRootPane);
    }

    public void restoreUiState() {
        UIStateService.getInstance().restoreLayoutState(machineRootPane);
    }

}