package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.ComplexEntity;
import be.doebi.aerismill.model.step.ComplexEntityPart;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;
import be.doebi.aerismill.model.step.geometry.BSplineSurfaceWithKnots;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ComplexEntityInterpreter {
    private static final Set<String> SUPPORTED_B_SPLINE_CURVE_PARTS = Set.of(
            "BOUNDED_CURVE",
            "B_SPLINE_CURVE",
            "B_SPLINE_CURVE_WITH_KNOTS",
            "CURVE",
            "GEOMETRIC_REPRESENTATION_ITEM",
            "RATIONAL_B_SPLINE_CURVE",
            "REPRESENTATION_ITEM"
    );
    private static final Set<String> SUPPORTED_B_SPLINE_SURFACE_PARTS = Set.of(
            "BOUNDED_SURFACE",
            "B_SPLINE_SURFACE",
            "B_SPLINE_SURFACE_WITH_KNOTS",
            "GEOMETRIC_REPRESENTATION_ITEM",
            "RATIONAL_B_SPLINE_SURFACE",
            "REPRESENTATION_ITEM",
            "SURFACE"
    );

    public void normalize(StepModel model) {
        Objects.requireNonNull(model, "model must not be null");

        for (StepEntity entity : model.getEntities()) {
            if (!(entity instanceof ComplexEntity complexEntity)) {
                continue;
            }

            StepEntity normalized = interpret(complexEntity);
            if (normalized != complexEntity) {
                model.addEntity(normalized);
            }
        }
    }

    public StepEntity interpret(ComplexEntity complexEntity) {
        Objects.requireNonNull(complexEntity, "complexEntity must not be null");

        StepEntity normalizedCurve = tryInterpretRationalBSplineCurve(complexEntity);
        if (normalizedCurve != null) {
            return normalizedCurve;
        }

        StepEntity normalizedSurface = tryInterpretRationalBSplineSurface(complexEntity);
        if (normalizedSurface != null) {
            return normalizedSurface;
        }

        return complexEntity;
    }

    private StepEntity tryInterpretRationalBSplineCurve(ComplexEntity complexEntity) {
        List<ComplexEntityPart> parts = complexEntity.getParts();
        if (parts.isEmpty()) {
            return null;
        }
        if (!parts.stream().allMatch(part -> SUPPORTED_B_SPLINE_CURVE_PARTS.contains(part.getType()))) {
            return null;
        }

        ComplexEntityPart splinePart = findPart(parts, "B_SPLINE_CURVE");
        ComplexEntityPart knotPart = findPart(parts, "B_SPLINE_CURVE_WITH_KNOTS");
        if (splinePart == null || knotPart == null) {
            return null;
        }

        ComplexEntityPart rationalPart = findPart(parts, "RATIONAL_B_SPLINE_CURVE");
        ComplexEntityPart representationPart = findPart(parts, "REPRESENTATION_ITEM");

        if (splinePart.getParams().size() != 5 || knotPart.getParams().size() != 3) {
            throw new IllegalArgumentException("Unsupported B-spline complex entity layout");
        }
        if (rationalPart != null && rationalPart.getParams().size() != 1) {
            throw new IllegalArgumentException("Unsupported rational B-spline complex entity layout");
        }
        if (representationPart != null && representationPart.getParams().size() != 1) {
            throw new IllegalArgumentException("Unsupported representation item layout");
        }

        String name = representationPart == null
                ? ""
                : StepParserUtils.parseStepString(representationPart.getParams().getFirst());

        int degree = Integer.parseInt(splinePart.getParams().get(0).trim());
        List<String> controlPointRefs = StepParserUtils.parseReferenceList(splinePart.getParams().get(1));
        String curveForm = splinePart.getParams().get(2).trim();
        StepLogical closedCurve = StepParserUtils.parseStepLogical(splinePart.getParams().get(3));
        StepLogical selfIntersect = StepParserUtils.parseStepLogical(splinePart.getParams().get(4));
        List<Integer> knotMultiplicities = StepParserUtils.parseIntegerList(knotPart.getParams().get(0));
        List<Double> knots = StepParserUtils.parseDoubleList(knotPart.getParams().get(1));
        String knotSpec = knotPart.getParams().get(2).trim();
        List<Double> weights = rationalPart == null
                ? null
                : StepParserUtils.parseDoubleList(rationalPart.getParams().getFirst());

        return new BSplineCurveWithKnots(
                complexEntity.getId(),
                complexEntity.getRawParameters(),
                name,
                degree,
                controlPointRefs,
                curveForm,
                closedCurve,
                selfIntersect,
                knotMultiplicities,
                knots,
                knotSpec,
                weights
        );
    }

    private StepEntity tryInterpretRationalBSplineSurface(ComplexEntity complexEntity) {
        List<ComplexEntityPart> parts = complexEntity.getParts();
        if (parts.isEmpty()) {
            return null;
        }
        if (!parts.stream().allMatch(part -> SUPPORTED_B_SPLINE_SURFACE_PARTS.contains(part.getType()))) {
            return null;
        }

        ComplexEntityPart surfacePart = findPart(parts, "B_SPLINE_SURFACE");
        ComplexEntityPart knotPart = findPart(parts, "B_SPLINE_SURFACE_WITH_KNOTS");
        ComplexEntityPart rationalPart = findPart(parts, "RATIONAL_B_SPLINE_SURFACE");
        ComplexEntityPart representationPart = findPart(parts, "REPRESENTATION_ITEM");
        ComplexEntityPart boundedSurfacePart = findPart(parts, "BOUNDED_SURFACE");
        ComplexEntityPart abstractSurfacePart = findPart(parts, "SURFACE");

        if (surfacePart == null || knotPart == null || rationalPart == null) {
            return null;
        }
        if (boundedSurfacePart == null || abstractSurfacePart == null) {
            return null;
        }

        if (surfacePart.getParams().size() != 7 || knotPart.getParams().size() != 5) {
            throw new IllegalArgumentException("Unsupported B-spline surface complex entity layout");
        }
        if (rationalPart.getParams().size() != 1) {
            throw new IllegalArgumentException("Unsupported rational B-spline surface complex entity layout");
        }
        if (representationPart != null && representationPart.getParams().size() != 1) {
            throw new IllegalArgumentException("Unsupported representation item layout");
        }

        String name = representationPart == null
                ? ""
                : StepParserUtils.parseStepString(representationPart.getParams().getFirst());

        int uDegree = Integer.parseInt(surfacePart.getParams().get(0).trim());
        int vDegree = Integer.parseInt(surfacePart.getParams().get(1).trim());
        List<List<String>> controlPointRefs = parseReferenceGrid(surfacePart.getParams().get(2));
        String surfaceForm = surfacePart.getParams().get(3).trim();
        StepLogical uClosed = StepParserUtils.parseStepLogical(surfacePart.getParams().get(4));
        StepLogical vClosed = StepParserUtils.parseStepLogical(surfacePart.getParams().get(5));
        StepLogical selfIntersect = StepParserUtils.parseStepLogical(surfacePart.getParams().get(6));
        List<Integer> uMultiplicities = StepParserUtils.parseIntegerList(knotPart.getParams().get(0));
        List<Integer> vMultiplicities = StepParserUtils.parseIntegerList(knotPart.getParams().get(1));
        List<Double> uKnots = StepParserUtils.parseDoubleList(knotPart.getParams().get(2));
        List<Double> vKnots = StepParserUtils.parseDoubleList(knotPart.getParams().get(3));
        String knotSpec = knotPart.getParams().get(4).trim();
        List<List<Double>> weights = parseDoubleGrid(rationalPart.getParams().getFirst());

        return new BSplineSurfaceWithKnots(
                complexEntity.getId(),
                complexEntity.getRawParameters(),
                name,
                uDegree,
                vDegree,
                controlPointRefs,
                surfaceForm,
                uClosed,
                vClosed,
                selfIntersect,
                uMultiplicities,
                vMultiplicities,
                uKnots,
                vKnots,
                knotSpec,
                weights
        );
    }

    private List<List<String>> parseReferenceGrid(String value) {
        String inside = StepParserUtils.stripOuterParens(value);
        List<String> rowStrings = StepParserUtils.splitTopLevelGroups(inside);
        List<List<String>> grid = new java.util.ArrayList<>();

        for (String rowString : rowStrings) {
            grid.add(StepParserUtils.parseReferenceList(rowString));
        }

        return List.copyOf(grid);
    }

    private List<List<Double>> parseDoubleGrid(String value) {
        String inside = StepParserUtils.stripOuterParens(value);
        List<String> rowStrings = StepParserUtils.splitTopLevelGroups(inside);
        List<List<Double>> grid = new java.util.ArrayList<>();

        for (String rowString : rowStrings) {
            grid.add(List.copyOf(StepParserUtils.parseDoubleList(rowString)));
        }

        return List.copyOf(grid);
    }

    private ComplexEntityPart findPart(List<ComplexEntityPart> parts, String type) {
        for (ComplexEntityPart part : parts) {
            if (type.equals(part.getType())) {
                return part;
            }
        }
        return null;
    }
}
