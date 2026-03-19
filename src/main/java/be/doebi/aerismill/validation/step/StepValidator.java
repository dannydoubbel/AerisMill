package be.doebi.aerismill.validation.step;

public class StepValidator {
    public boolean isProbablyStepFile(String rawContent) {
        return rawContent != null
                && rawContent.contains("ISO-10303-21;")
                && rawContent.contains("HEADER;")
                && rawContent.contains("DATA;");
    }
}
