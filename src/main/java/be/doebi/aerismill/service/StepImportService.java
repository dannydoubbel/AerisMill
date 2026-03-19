package be.doebi.aerismill.service;

import be.doebi.aerismill.io.step.StepReader;
import be.doebi.aerismill.model.StepModel;

import java.io.File;

public class StepImportService {
    private final StepReader stepReader = new StepReader();


    public void open(File file) {
        System.out.println("INSIDE SERVICE StepImportService");

        StepModel model = stepReader.read(file);
        System.out.println("Returned model = " + model);

        // later store/update current document

    }
}
