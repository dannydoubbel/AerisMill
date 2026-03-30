package be.doebi.aerismill.service;

import be.doebi.aerismill.io.step.StepReader;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.parser.step.StepParser;

import java.io.File;

public class StepImportService {
    private final StepReader stepReader = new StepReader();
    private final StepParser stepParser = new StepParser();


    public StepModel open(File file) {

        String rawContent = stepReader.read(file);
        return stepParser.parse(file, rawContent);

        // later store/update current document

    }
}
