package be.doebi.aerismill.parser.step;


import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.BSplineSurfaceWithKnots;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class BSplineSurfaceWithKnotsParserTest {
    @Test
    void parseBSplineSurfaceWithKnots_shouldParseCorrectly() {
        StepEntity cp1 = new StepEntity("#1", StepEntityType.CARTESIAN_POINT.getName(), "( 'NONE', ( 0.0, 0.0, 0.0 ) )");
        StepEntity cp2 = new StepEntity("#2", StepEntityType.CARTESIAN_POINT.getName(), "( 'NONE', ( 1.0, 0.0, 0.0 ) )");
        StepEntity cp3 = new StepEntity("#3", StepEntityType.CARTESIAN_POINT.getName(), "( 'NONE', ( 0.0, 1.0, 0.0 ) )");
        StepEntity cp4 = new StepEntity("#4", StepEntityType.CARTESIAN_POINT.getName(), "( 'NONE', ( 1.0, 1.0, 0.0 ) )");

        Map<String, Object> parsedEntities = Map.of(
                "#1", cp1,
                "#2", cp2,
                "#3", cp3,
                "#4", cp4
        );

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS.getName(),
                "( dummy raw parameters )"
        );

        List<String> params = List.of(
                "2",
                "2",
                "((#1,#2),(#3,#4))",
                ".UNSPECIFIED.",
                ".F.",
                ".F.",
                ".F.",
                "(3,3)",
                "(3,3)",
                "(0.0,1.0)",
                "(0.0,1.0)",
                ".UNSPECIFIED."
        );

        BSplineSurfaceWithKnotsParser parser = new BSplineSurfaceWithKnotsParser();

        BSplineSurfaceWithKnots result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals(2, result.getUDegree());
        assertEquals(2, result.getVDegree());
        assertEquals(2, result.getControlPointsList().size());
        assertEquals(2, result.getControlPointsList().get(0).size());
        assertEquals(".UNSPECIFIED.", result.getSurfaceForm());
        assertEquals(false, result.isUClosed());
        assertEquals(false, result.isVClosed());
        assertEquals(false, result.isSelfIntersect());
        assertEquals(List.of(3, 3), result.getUMultiplicities());
        assertEquals(List.of(3, 3), result.getVMultiplicities());
        assertEquals(List.of(0.0, 1.0), result.getUKnots());
        assertEquals(List.of(0.0, 1.0), result.getVKnots());
        assertEquals(".UNSPECIFIED.", result.getKnotSpec());
    }
}