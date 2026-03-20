package be.doebi.aerismill.parser.step;

import org.junit.jupiter.api.Test;

import be.doebi.aerismill.model.step.StepEntity;

import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class BSplineCurveWithKnotsParserTest {
    @Test
    void parseBSplineCurveWithKnots_shouldParseCorrectly() {
        StepEntity cp1 = new StepEntity(
                "#1",
                StepEntityType.CARTESIAN_POINT.getName(),
                "( 'NONE', ( 0.0, 0.0, 0.0 ) )"
        );
        StepEntity cp2 = new StepEntity(
                "#2",
                StepEntityType.CARTESIAN_POINT.getName(),
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )"
        );
        StepEntity cp3 = new StepEntity(
                "#3",
                StepEntityType.CARTESIAN_POINT.getName(),
                "( 'NONE', ( 2.0, 0.0, 0.0 ) )"
        );

        Map<String, Object> parsedEntities = Map.of(
                "#1", cp1,
                "#2", cp2,
                "#3", cp3
        );

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.B_SPLINE_CURVE_WITH_KNOTS.getName(),
                "( dummy raw parameters )"
        );

        List<String> params = List.of(
                "2",
                "(#1,#2,#3)",
                ".UNSPECIFIED.",
                ".F.",
                ".F.",
                "(3,3)",
                "(0.0,1.0)",
                ".UNSPECIFIED."
        );

        BSplineCurveWithKnotsParser parser = new BSplineCurveWithKnotsParser();
        BSplineCurveWithKnots result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals(2, result.getDegree());
        assertEquals(3, result.getControlPoints().size());
        assertEquals(".UNSPECIFIED.", result.getCurveForm());
        assertEquals(false, result.isClosedCurve());
        assertEquals(false, result.isSelfIntersect());
        assertEquals(List.of(3, 3), result.getKnotMultiplicities());
        assertEquals(List.of(0.0, 1.0), result.getKnots());
        assertEquals(".UNSPECIFIED.", result.getKnotSpec());
    }

}