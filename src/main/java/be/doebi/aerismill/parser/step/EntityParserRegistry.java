package be.doebi.aerismill.parser.step;



import be.doebi.aerismill.model.step.admin.*;

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

        parsers.put(StepEntityType.APPLICATION_CONTEXT,
                new AdministrativeStepEntityParser<>(ApplicationContext::new));

        parsers.put(StepEntityType.APPLICATION_PROTOCOL_DEFINITION,
                new AdministrativeStepEntityParser<>(ApplicationProtocolDefinition::new));

        parsers.put(StepEntityType.APPROVAL,
                new AdministrativeStepEntityParser<>(Approval::new));

        parsers.put(StepEntityType.APPROVAL_DATE_TIME,
                new AdministrativeStepEntityParser<>(ApprovalDateTime::new));

        parsers.put(StepEntityType.APPROVAL_PERSON_ORGANIZATION,
                new AdministrativeStepEntityParser<>(ApprovalPersonOrganization::new));

        parsers.put(StepEntityType.APPROVAL_ROLE,
                new AdministrativeStepEntityParser<>(ApprovalRole::new));

        parsers.put(StepEntityType.APPROVAL_STATUS,
                new AdministrativeStepEntityParser<>(ApprovalStatus::new));

        parsers.put(StepEntityType.CALENDAR_DATE,
                new AdministrativeStepEntityParser<>(CalendarDate::new));

        parsers.put(StepEntityType.DATE_AND_TIME,
                new AdministrativeStepEntityParser<>(DateAndTime::new));

        parsers.put(StepEntityType.DATE_TIME_ROLE,
                new AdministrativeStepEntityParser<>(DateTimeRole::new));

        parsers.put(StepEntityType.DOCUMENT,
                new AdministrativeStepEntityParser<>(Document::new));

        parsers.put(StepEntityType.DOCUMENT_TYPE,
                new AdministrativeStepEntityParser<>(DocumentType::new));

        parsers.put(StepEntityType.LOCAL_TIME,
                new AdministrativeStepEntityParser<>(LocalTime::new));

        parsers.put(StepEntityType.ORGANIZATION,
                new AdministrativeStepEntityParser<>(Organization::new));

        parsers.put(StepEntityType.PERSON,
                new AdministrativeStepEntityParser<>(Person::new));

        parsers.put(StepEntityType.PERSONAL_ADDRESS,
                new AdministrativeStepEntityParser<>(PersonalAddress::new));

        parsers.put(StepEntityType.PERSON_AND_ORGANIZATION,
                new AdministrativeStepEntityParser<>(PersonAndOrganization::new));

        parsers.put(StepEntityType.PERSON_AND_ORGANIZATION_ROLE,
                new AdministrativeStepEntityParser<>(PersonAndOrganizationRole::new));

        parsers.put(StepEntityType.SECURITY_CLASSIFICATION,
                new AdministrativeStepEntityParser<>(SecurityClassification::new));

        parsers.put(StepEntityType.SECURITY_CLASSIFICATION_LEVEL,
                new AdministrativeStepEntityParser<>(SecurityClassificationLevel::new));

        parsers.put(StepEntityType.PRODUCT_CATEGORY,
                new AdministrativeStepEntityParser<>(ProductCategory::new));

        parsers.put(StepEntityType.PRODUCT_CATEGORY_RELATIONSHIP,
                new AdministrativeStepEntityParser<>(ProductCategoryRelationship::new));
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
