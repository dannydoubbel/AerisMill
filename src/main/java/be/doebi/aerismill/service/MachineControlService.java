package be.doebi.aerismill.service;

import be.doebi.aerismill.ui.AppConsole;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MachineControlService {

    private SerialPort activePort;
    private final StringBuilder serialReceiveBuffer = new StringBuilder();

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

        try {
            activePort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }

                    int available = activePort.bytesAvailable();
                    if (available <= 0) {
                        return;
                    }

                    byte[] buffer = new byte[available];
                    int numRead = activePort.readBytes(buffer, buffer.length);

                    if (numRead > 0) {
                        String received = new String(buffer, 0, numRead, StandardCharsets.UTF_8);
                        onSerialDataReceived(received);
                    }
                }
            });
        } catch (Exception e) {
            AppConsole.log("[MachineService] DataListener not added to the com port " +e);

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

    private void handleIncomingSerialLine(String line) {
        AppConsole.log("[RX] " + line);

        // later:
        // route to COM console
        // detect <...> DRO status
        // parse ok / error / alarm
    }

    private void onSerialDataReceived(String chunk) {
        serialReceiveBuffer.append(chunk);

        int newlineIndex;
        while ((newlineIndex = serialReceiveBuffer.indexOf("\n")) >= 0) {
            String line = serialReceiveBuffer.substring(0, newlineIndex).trim();
            serialReceiveBuffer.delete(0, newlineIndex + 1);

            if (!line.isEmpty()) {
                handleIncomingSerialLine(line);
            }
        }
    }

    public boolean isConnected() {
        return activePort != null && activePort.isOpen();
    }

    public void spindleStart(int rpm) {
        ensureConnected();
        sendCommand("spindleStart", "M3");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        sendCommand("spindleStart", "S" + rpm);
    }

    public void spindleStop() {
        ensureConnected();
        sendCommand("spindleStop", "M5");
    }

    public void setSpindleSpeed(int rpm) {
        ensureConnected();
        sendCommand("setSpindleSpeed", "s" + rpm);
        System.out.println("s" + rpm);
    }

    public void sendCommand(String demander, String command) {
        ensureConnected();
        AppConsole.log("[MachineService] " + demander + " TX >>> " + command);
        sendRaw(command + "\r\n");
    }

    public void sendRaw(String raw) {
        try {
            OutputStream outputStream = activePort.getOutputStream();
            outputStream.write(raw.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            AppConsole.log("RAW  " + raw);
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