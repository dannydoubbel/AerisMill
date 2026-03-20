package be.doebi.aerismill.parser.step;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum StepEntityType {
    CARTESIAN_POINT("CARTESIAN_POINT"),
    DIRECTION("DIRECTION"),
    VECTOR("VECTOR"),
    AXIS2_PLACEMENT_3D("AXIS2_PLACEMENT_3D"),
    LINE("LINE"),
    CIRCLE("CIRCLE"),
    ELLIPSE("ELLIPSE"),
    PLANE("PLANE"),
    CYLINDRICAL_SURFACE("CYLINDRICAL_SURFACE"),
    CONICAL_SURFACE("CONICAL_SURFACE"),
    TOROIDAL_SURFACE("TOROIDAL_SURFACE"),
    B_SPLINE_CURVE_WITH_KNOTS("B_SPLINE_CURVE_WITH_KNOTS"),
    B_SPLINE_SURFACE_WITH_KNOTS("B_SPLINE_SURFACE_WITH_KNOTS"),
    EDGE_CURVE("EDGE_CURVE"),
    ORIENTED_EDGE("ORIENTED_EDGE"),
    EDGE_LOOP("EDGE_LOOP"),
    FACE_BOUND("FACE_BOUND"),
    FACE_OUTER_BOUND("FACE_OUTER_BOUND"),
    ADVANCED_FACE("ADVANCED_FACE"),
    CLOSED_SHELL("CLOSED_SHELL"),
    MANIFOLD_SOLID_BREP("MANIFOLD_SOLID_BREP"),
    ADVANCED_BREP_SHAPE_REPRESENTATION("ADVANCED_BREP_SHAPE_REPRESENTATION"),
    SHAPE_REPRESENTATION("SHAPE_REPRESENTATION"),
    SHAPE_DEFINITION_REPRESENTATION("SHAPE_DEFINITION_REPRESENTATION"),
    PRODUCT_DEFINITION_SHAPE("PRODUCT_DEFINITION_SHAPE"),
    GEOMETRIC_REPRESENTATION_CONTEXT("GEOMETRIC_REPRESENTATION_CONTEXT"),
    COMPLEX_ENTITY("COMPLEX_ENTITY");

    private final String name;

    StepEntityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, StepEntityType> BY_NAME =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(
                            StepEntityType::getName,
                            Function.identity()
                    ));

    public static StepEntityType fromName(String name) {
        StepEntityType type = BY_NAME.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Unknown STEP entity type: " + name);
        }
        return type;
    }

    public static StepEntityType fromNameOrNull(String name) {
        return BY_NAME.get(name);
    }

    public boolean matches(String rawTypeName) {
        return name.equals(rawTypeName);
    }

    @Override
    public String toString() {
        return name;
    }
}
