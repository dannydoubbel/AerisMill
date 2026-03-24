package be.doebi.aerismill.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DroPollingService {

    private final MachineControlService machineControlService;

    private ScheduledExecutorService droScheduler;
    private volatile boolean droPollingActive = false;

    public DroPollingService(MachineControlService machineControlService) {
        this.machineControlService = machineControlService;
    }

    public void startDroPolling() {
        if (droPollingActive) {
            return;
        }

        droPollingActive = true;
        droScheduler = Executors.newSingleThreadScheduledExecutor();

        droScheduler.scheduleAtFixedRate(() -> {
            if (!droPollingActive) {
                return;
            }

            try {
                machineControlService.sendRealtimeCommand("?");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void stopDroPolling() {
        droPollingActive = false;

        if (droScheduler != null) {
            droScheduler.shutdownNow();
            droScheduler = null;
        }
    }
}