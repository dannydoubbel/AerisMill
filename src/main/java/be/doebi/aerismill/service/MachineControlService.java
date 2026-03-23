package be.doebi.aerismill.service;

import be.doebi.aerismill.ui.AppConsole;
import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MachineControlService {
    private SerialPort activePort;

    public boolean connect(String portName, int baudRate) {
        disconnect();

        activePort = SerialPort.getCommPort(portName);
        activePort.setBaudRate(baudRate);
        activePort.setNumDataBits(8);
        activePort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        activePort.setParity(SerialPort.NO_PARITY);
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        boolean opened = activePort.openPort();

        if (!opened) {
            AppConsole.log("[MachineService] Failed to open port: " + portName);
            activePort = null;
            return false;
        }

        AppConsole.log("[MachineService] Port opened: " + portName + " @ " + baudRate);

        try {
            Thread.sleep(2000); // GRBL often resets on open
            sendRaw("\r\n");
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppConsole.log("[MachineService] Connect delay interrupted.");
        }

        return true;
    }

    public void disconnect() {
        if (activePort != null) {
            String portName = activePort.getSystemPortName();

            if (activePort.isOpen()) {
                activePort.closePort();
                AppConsole.log("[MachineService] Port closed: " + portName);
            }

            activePort = null;
        }
    }

    public boolean isConnected() {
        return activePort != null && activePort.isOpen();
    }

    public void spindleStart(int rpm) {
        ensureConnected();
        sendCommand("spindleStart","M3");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sendCommand("spindleStart" , "S" + rpm);
    }

    public void spindleStop() {
        ensureConnected();
        sendCommand("spindleStop","M5");
    }

    public void setSpindleSpeed(int rpm) {
        ensureConnected();
        sendCommand("setSpindleSpeed","s" + rpm);
        System.out.println("s" + rpm);
    }

    public void sendCommand(String demander,String command) {
        ensureConnected();
        AppConsole.log("[MachineService] " + demander + " TX >>> " + command);
        sendRaw(command + "\r\n");
    }

    private void sendRaw(String raw) {
        try {
            OutputStream outputStream = activePort.getOutputStream();
            outputStream.write(raw.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            AppConsole.log("RAW  "+raw);
        } catch (IOException e) {
            AppConsole.log("Failed to write to serial port" + e);
            throw new RuntimeException("Failed to write to serial port", e);
        }
    }

    private void ensureConnected() {
        if (!isConnected()) {
            throw new IllegalStateException("Machine is not connected.");
        }
    }
}