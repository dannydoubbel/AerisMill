package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.admin.*;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.presentation.*;

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
        parsers.put(StepEntityType.SHAPE_DEFINITION_REPRESENTATION, new ShapeDefinitionRepresentationParser());
        parsers.put(StepEntityType.PRODUCT_DEFINITION_SHAPE, new ProductDefinitionShapeParser());

        parsers.put(StepEntityType.APPLICATION_CONTEXT,
                new AdministrativeStepEntityParser<>(StepEntityType.APPLICATION_CONTEXT, ApplicationContext::new));

        parsers.put(StepEntityType.APPLICATION_PROTOCOL_DEFINITION,
                new AdministrativeStepEntityParser<>(StepEntityType.APPLICATION_PROTOCOL_DEFINITION, ApplicationProtocolDefinition::new));

        parsers.put(StepEntityType.APPROVAL,
                new AdministrativeStepEntityParser<>(StepEntityType.APPROVAL, Approval::new));

        parsers.put(StepEntityType.APPROVAL_DATE_TIME,
                new AdministrativeStepEntityParser<>(StepEntityType.APPROVAL_DATE_TIME, ApprovalDateTime::new));

        parsers.put(StepEntityType.APPROVAL_PERSON_ORGANIZATION,
                new AdministrativeStepEntityParser<>(StepEntityType.APPROVAL_PERSON_ORGANIZATION, ApprovalPersonOrganization::new));

        parsers.put(StepEntityType.APPROVAL_ROLE,
                new AdministrativeStepEntityParser<>(StepEntityType.APPROVAL_ROLE, ApprovalRole::new));

        parsers.put(StepEntityType.APPROVAL_STATUS,
                new AdministrativeStepEntityParser<>(StepEntityType.APPROVAL_STATUS, ApprovalStatus::new));

        parsers.put(StepEntityType.CALENDAR_DATE,
                new AdministrativeStepEntityParser<>(StepEntityType.CALENDAR_DATE, CalendarDate::new));

        parsers.put(StepEntityType.DATE_AND_TIME,
                new AdministrativeStepEntityParser<>(StepEntityType.DATE_AND_TIME, DateAndTime::new));

        parsers.put(StepEntityType.DATE_TIME_ROLE,
                new AdministrativeStepEntityParser<>(StepEntityType.DATE_TIME_ROLE, DateTimeRole::new));

        parsers.put(StepEntityType.DOCUMENT,
                new AdministrativeStepEntityParser<>(StepEntityType.DOCUMENT, Document::new));

        parsers.put(StepEntityType.DOCUMENT_TYPE,
                new AdministrativeStepEntityParser<>(StepEntityType.DOCUMENT_TYPE, DocumentType::new));

        parsers.put(StepEntityType.LOCAL_TIME,
                new AdministrativeStepEntityParser<>(StepEntityType.LOCAL_TIME, LocalTime::new));

        parsers.put(StepEntityType.ORGANIZATION,
                new AdministrativeStepEntityParser<>(StepEntityType.ORGANIZATION, Organization::new));

        parsers.put(StepEntityType.PERSON,
                new AdministrativeStepEntityParser<>(StepEntityType.PERSON, Person::new));

        parsers.put(StepEntityType.PERSONAL_ADDRESS,
                new AdministrativeStepEntityParser<>(StepEntityType.PERSONAL_ADDRESS, PersonalAddress::new));

        parsers.put(StepEntityType.PERSON_AND_ORGANIZATION,
                new AdministrativeStepEntityParser<>(StepEntityType.PERSON_AND_ORGANIZATION, PersonAndOrganization::new));

        parsers.put(StepEntityType.PERSON_AND_ORGANIZATION_ROLE,
                new AdministrativeStepEntityParser<>(StepEntityType.PERSON_AND_ORGANIZATION_ROLE, PersonAndOrganizationRole::new));

        parsers.put(StepEntityType.SECURITY_CLASSIFICATION,
                new AdministrativeStepEntityParser<>(StepEntityType.SECURITY_CLASSIFICATION, SecurityClassification::new));

        parsers.put(StepEntityType.SECURITY_CLASSIFICATION_LEVEL,
                new AdministrativeStepEntityParser<>(StepEntityType.SECURITY_CLASSIFICATION_LEVEL, SecurityClassificationLevel::new));

        parsers.put(StepEntityType.PRODUCT_CATEGORY,
                new AdministrativeStepEntityParser<>(StepEntityType.PRODUCT_CATEGORY, ProductCategory::new));

        parsers.put(StepEntityType.PRODUCT_CATEGORY_RELATIONSHIP,
                new AdministrativeStepEntityParser<>(StepEntityType.PRODUCT_CATEGORY_RELATIONSHIP, ProductCategoryRelationship::new));

        parsers.put(StepEntityType.COLOUR_RGB,
                new PresentationStepEntityParser<>(StepEntityType.COLOUR_RGB, ColourRgb::new));

        parsers.put(StepEntityType.CURVE_STYLE,
                new PresentationStepEntityParser<>(StepEntityType.CURVE_STYLE, CurveStyle::new));

        parsers.put(StepEntityType.DRAUGHTING_PRE_DEFINED_COLOUR,
                new PresentationStepEntityParser<>(StepEntityType.DRAUGHTING_PRE_DEFINED_COLOUR, DraughtingPreDefinedColour::new));

        parsers.put(StepEntityType.DRAUGHTING_PRE_DEFINED_CURVE_FONT,
                new PresentationStepEntityParser<>(StepEntityType.DRAUGHTING_PRE_DEFINED_CURVE_FONT, DraughtingPreDefinedCurveFont::new));

        parsers.put(StepEntityType.FILL_AREA_STYLE,
                new PresentationStepEntityParser<>(StepEntityType.FILL_AREA_STYLE, FillAreaStyle::new));

        parsers.put(StepEntityType.FILL_AREA_STYLE_COLOUR,
                new PresentationStepEntityParser<>(StepEntityType.FILL_AREA_STYLE_COLOUR, FillAreaStyleColour::new));

        parsers.put(StepEntityType.PRESENTATION_LAYER_ASSIGNMENT,
                new PresentationStepEntityParser<>(StepEntityType.PRESENTATION_LAYER_ASSIGNMENT, PresentationLayerAssignment::new));

        parsers.put(StepEntityType.PRESENTATION_STYLE_ASSIGNMENT,
                new PresentationStepEntityParser<>(StepEntityType.PRESENTATION_STYLE_ASSIGNMENT, PresentationStyleAssignment::new));

        parsers.put(StepEntityType.STYLED_ITEM,
                new PresentationStepEntityParser<>(StepEntityType.STYLED_ITEM, StyledItem::new));

        parsers.put(StepEntityType.OVER_RIDING_STYLED_ITEM,
                new PresentationStepEntityParser<>(StepEntityType.OVER_RIDING_STYLED_ITEM, OverRidingStyledItem::new));

        parsers.put(StepEntityType.SURFACE_SIDE_STYLE,
                new PresentationStepEntityParser<>(StepEntityType.SURFACE_SIDE_STYLE, SurfaceSideStyle::new));

        parsers.put(StepEntityType.SURFACE_STYLE_FILL_AREA,
                new PresentationStepEntityParser<>(StepEntityType.SURFACE_STYLE_FILL_AREA, SurfaceStyleFillArea::new));

        parsers.put(StepEntityType.SURFACE_STYLE_USAGE,
                new PresentationStepEntityParser<>(StepEntityType.SURFACE_STYLE_USAGE, SurfaceStyleUsage::new));

        parsers.put(StepEntityType.MECHANICAL_DESIGN_GEOMETRIC_PRESENTATION_REPRESENTATION,
                new PresentationStepEntityParser<>(StepEntityType.MECHANICAL_DESIGN_GEOMETRIC_PRESENTATION_REPRESENTATION, MechanicalDesignGeometricPresentationRepresentation::new));

        parsers.put(StepEntityType.VERTEX_POINT, new VertexPointParser());
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