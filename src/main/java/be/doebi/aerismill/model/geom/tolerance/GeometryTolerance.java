package be.doebi.aerismill.model.geom.tolerance;

public record GeometryTolerance(
        double pointEqualityEpsilon,
        double chordalDeflection,
        double angularDeflectionRadians,
        int minCurveSegments,
        int maxCurveSegments
) {
    public static GeometryTolerance defaults() {
        return new GeometryTolerance(
                1e-6,
                0.1,
                Math.toRadians(10.0),
                8,
                512
        );
    }
}