package be.doebi.aerismill.service;

import be.doebi.aerismill.io.step.StepReader;
import be.doebi.aerismill.model.step.core.StepModel;
import be.doebi.aerismill.parser.step.StepParser;

import java.io.File;

public class StepImportService {
    private final StepReader stepReader = new StepReader();
    private final StepParser stepParser = new StepParser();


    public StepModel open(File file) {
        System.out.println("INSIDE SERVICE StepImportService");
        String rawContent = stepReader.read(file);
        return stepParser.parse(file, rawContent);

        // later store/update current document

    }
}
