package be.doebi.aerismill.model;

public class StepModel {
    private final String sourceName;

    public StepModel(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }

    @Override
    public String toString() {
        return "StepModel{sourceName='" + sourceName + "'}";
    }
}
