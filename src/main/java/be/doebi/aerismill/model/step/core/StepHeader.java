package be.doebi.aerismill.model.step.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StepHeader {
    private final List<String> rawHeaderLines = new ArrayList<>();

    public void addLine(String line) {
        if (line != null && !line.isBlank()) {
            rawHeaderLines.add(line);
        }
    }

    public List<String> getRawHeaderLines() {
        return Collections.unmodifiableList(rawHeaderLines);
    }
}