package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.model.step.geometry.BSplineSurfaceWithKnots;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BSplineSurfaceWithKnotsParserTest {
    @Test
    void parseBSplineSurfaceWithKnots_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS,
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
        BSplineSurfaceWithKnots result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals(2, result.getUDegree());
        assertEquals(2, result.getVDegree());

        assertEquals(2, result.getControlPointRefs().size());
        assertEquals(List.of("#1", "#2"), result.getControlPointRefs().get(0));
        assertEquals(List.of("#3", "#4"), result.getControlPointRefs().get(1));

        assertTrue(result.getControlPointsList().isEmpty());

        assertEquals(".UNSPECIFIED.", result.getSurfaceForm());
        assertEquals(StepLogical.FALSE,result.isUClosed());
        assertEquals(StepLogical.FALSE,result.isVClosed());
        assertEquals(StepLogical.FALSE,result.isSelfIntersect());
        assertEquals(List.of(3, 3), result.getUMultiplicities());
        assertEquals(List.of(3, 3), result.getVMultiplicities());
        assertEquals(List.of(0.0, 1.0), result.getUKnots());
        assertEquals(List.of(0.0, 1.0), result.getVKnots());
        assertEquals(".UNSPECIFIED.", result.getKnotSpec());
    }
}