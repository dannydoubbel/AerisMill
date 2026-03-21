package be.doebi.aerismill.ui;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class MachineController {

    @FXML
    private ComboBox<String> portComboBox;

    @FXML
    private Label machineStatusLabel;

    @FXML
    public void initialize() {
        refreshAvailablePorts();
    }

    @FXML
    public void onRefreshPorts() {
        refreshAvailablePorts();
    }

    @FXML
    public void onConnectMachine() {
        String selectedPort = portComboBox.getValue();

        if (selectedPort == null || selectedPort.isBlank()) {
            machineStatusLabel.setText("No port selected");
            AppConsole.log("[Machine] Connect clicked, but no port selected.");
            return;
        }

        machineStatusLabel.setText("Selected: " + selectedPort);
        AppConsole.log("[Machine] Connect clicked for port: " + selectedPort);
    }

    @FXML
    public void onDisconnectMachine() {
        machineStatusLabel.setText("Disconnected");
        AppConsole.log("[Machine] Disconnect clicked.");
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
        AppConsole.log("[Machine] Spindle start clicked.");
    }

    @FXML
    public void onSpindleStop() {
        AppConsole.log("[Machine] Spindle stop clicked.");
    }

    @FXML
    public void onSpindleSpeedDown() {
        AppConsole.log("[Machine] Spindle speed down clicked.");
    }

    @FXML
    public void onSpindleSpeedUp() {
        AppConsole.log("[Machine] Spindle speed up clicked.");
    }

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
}