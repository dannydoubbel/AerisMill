package be.doebi.aerismill.parser.step;



import java.util.EnumMap;
import java.util.Map;


public class EntityParserRegistry {
    private final Map<StepEntityType, EntityParser> parsers = new EnumMap<>(StepEntityType.class);

    public EntityParserRegistry() {
        parsers.put(StepEntityType.CARTESIAN_POINT, new CartesianPointParser());
        parsers.put(StepEntityType.DIRECTION, new DirectionParser());
        parsers.put(StepEntityType.VECTOR, new VectorParser());
        parsers.put(StepEntityType.AXIS2_PLACEMENT_3D, new Axis2Placement3DParser());
        parsers.put(StepEntityType.LINE, new LineParser());
        parsers.put(StepEntityType.CIRCLE, new CircleParser());

        parsers.put(StepEntityType.EDGE_CURVE, new EdgeCurveParser());
        parsers.put(StepEntityType.ORIENTED_EDGE, new OrientedEdgeParser());
        parsers.put(StepEntityType.EDGE_LOOP, new EdgeLoopParser());
        parsers.put(StepEntityType.FACE_BOUND, new FaceBoundParser());
        parsers.put(StepEntityType.FACE_OUTER_BOUND, new FaceOuterBoundParser());

        parsers.put(StepEntityType.PLANE, new PlaneParser());
        parsers.put(StepEntityType.CYLINDRICAL_SURFACE, new CylindricalSurfaceParser());
        parsers.put(StepEntityType.CONICAL_SURFACE, new ConicalSurfaceParser());

        parsers.put(StepEntityType.ADVANCED_FACE, new AdvancedFaceParser());
        parsers.put(StepEntityType.CLOSED_SHELL, new ClosedShellParser());

        parsers.put(StepEntityType.MANIFOLD_SOLID_BREP, new ManifoldSolidBrepParser());
        parsers.put(StepEntityType.ADVANCED_BREP_SHAPE_REPRESENTATION, new AdvancedBrepShapeRepresentationParser());
        parsers.put(StepEntityType.TOROIDAL_SURFACE, new ToroidalSurfaceParser());
        parsers.put(StepEntityType.B_SPLINE_CURVE_WITH_KNOTS, new BSplineCurveWithKnotsParser());
        parsers.put(StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS, new BSplineSurfaceWithKnotsParser());
        parsers.put(StepEntityType.COMPLEX_ENTITY, new ComplexEntityParser());
        parsers.put(StepEntityType.SHAPE_REPRESENTATION, new ShapeRepresentationParser());
        parsers.put(StepEntityType.SHAPE_DEFINITION_REPRESENTATION, new be.doebi.aerismill.parser.step.ShapeRepresentationParser());
        parsers.put(StepEntityType.PRODUCT_DEFINITION_SHAPE, new ProductDefinitionShapeParser());
    }

    public EntityParser get(StepEntityType type) {
        return parsers.get(type);
    }

    public boolean supports(StepEntityType type) {
        return parsers.containsKey(type);
    }

    public Map<StepEntityType, EntityParser> getAll() {
        return Map.copyOf(parsers);
    }
}
