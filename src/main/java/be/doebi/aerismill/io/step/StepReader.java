package be.doebi.aerismill.io.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StepReader {

    public String read(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read STEP file: " + file.getAbsolutePath(), e);
        }
    }
}
