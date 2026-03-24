package be.doebi.aerismill.machine;

public class MachineStatus {
    private final String state;
    private final double machineX;
    private final double machineY;
    private final double machineZ;
    private final double machineA;

    public MachineStatus(String state, double machineX, double machineY, double machineZ, double machineA) {
        this.state = state;
        this.machineX = machineX;
        this.machineY = machineY;
        this.machineZ = machineZ;
        this.machineA = machineA;
    }

    public String getState() { return state; }
    public double getMachineX() { return machineX; }
    public double getMachineY() { return machineY; }
    public double getMachineZ() { return machineZ; }
    public double getMachineA() { return machineA; }
}