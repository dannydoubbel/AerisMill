package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.parser.step.geometry.BSplineCurveWithKnotsParser;
import org.junit.jupiter.api.Test;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class BSplineCurveWithKnotsParserTest {
    @Test
    void parseBSplineCurveWithKnots_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.B_SPLINE_CURVE_WITH_KNOTS,
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
        BSplineCurveWithKnots result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals(2, result.getDegree());

        assertEquals(List.of("#1", "#2", "#3"), result.getControlPointRefs());
        assertNull(result.getControlPoints());

        assertEquals(".UNSPECIFIED.", result.getCurveForm());
        assertEquals(StepLogical.FALSE, result.isClosedCurve());
        assertEquals(StepLogical.FALSE, result.isSelfIntersect());
        assertEquals(List.of(3, 3), result.getKnotMultiplicities());
        assertEquals(List.of(0.0, 1.0), result.getKnots());
        assertEquals(".UNSPECIFIED.", result.getKnotSpec());
    }
}