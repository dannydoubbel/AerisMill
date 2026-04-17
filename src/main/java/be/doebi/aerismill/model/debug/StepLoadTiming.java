package be.doebi.aerismill.model.debug;


public record StepLoadTiming(
        long importMillis,
        long assemblyMillis,
        long meshMillis,
        long totalMillis
) {
}