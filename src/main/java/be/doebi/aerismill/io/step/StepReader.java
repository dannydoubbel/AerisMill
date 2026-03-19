package be.doebi.aerismill.io.step;

import be.doebi.aerismill.model.StepModel;

import java.io.File;

public class StepReader {
    public StepModel read(File file) {
        System.out.println("Inside IO STEP StepReader");
        System.out.println("Opening STEP file: " + file.getAbsolutePath());

        return new StepModel(file.getName());
    }
}
