package be.doebi.aerismill.model.step;

import be.doebi.aerismill.model.step.base.StepEntity;

import java.io.File;
import java.util.List;

public class StepModel {
    private final File sourceFile;
    private final String name;
    private final String rawContent;
    private final List<StepEntity> entities;

    public StepModel(File sourceFile, String name, String rawContent, List<StepEntity> entities) {
        this.sourceFile = sourceFile;
        this.name = name;
        this.rawContent = rawContent;
        this.entities = entities;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getName() {
        return name;
    }

    public String getRawContent() {
        return rawContent;
    }

    public List<StepEntity> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        return "StepModel{sourceName='" + name + "'}";
    }
}
