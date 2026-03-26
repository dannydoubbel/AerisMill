package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.ComplexEntity;
import be.doebi.aerismill.model.step.ComplexEntityPart;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComplexEntityParserTest {
    @Test
    void parseComplexEntity_shouldParsePartsCorrectly() {
        StepEntity entity = new StepEntity(
                "#15013",
                StepEntityType.COMPLEX_ENTITY,
                "( BOUNDED_CURVE ( ) " +
                        "B_SPLINE_CURVE ( 2, ( #1, #2, #3 ), .UNSPECIFIED., .F., .F. ) " +
                        "B_SPLINE_CURVE_WITH_KNOTS ( ( 3, 3 ), ( 0.0, 1.0 ), .UNSPECIFIED. ) )"
        );

        ComplexEntityParser parser = new ComplexEntityParser();
        ComplexEntity result = parser.parse(entity, List.of(), Map.of());

        assertEquals("#15013", result.getId());
        assertEquals(3, result.getParts().size());

        assertEquals("BOUNDED_CURVE", result.getParts().get(0).getType());
        assertEquals(0, result.getParts().get(0).getParams().size());

        assertEquals("B_SPLINE_CURVE", result.getParts().get(1).getType());
        assertEquals(5, result.getParts().get(1).getParams().size());
        assertEquals("2", result.getParts().get(1).getParams().get(0));
        assertEquals("( #1, #2, #3 )", result.getParts().get(1).getParams().get(1));

        assertEquals("B_SPLINE_CURVE_WITH_KNOTS", result.getParts().get(2).getType());
        assertEquals(3, result.getParts().get(2).getParams().size());
        assertEquals("( 3, 3 )", result.getParts().get(2).getParams().get(0));
        assertEquals("( 0.0, 1.0 )", result.getParts().get(2).getParams().get(1));
        assertEquals(".UNSPECIFIED.", result.getParts().get(2).getParams().get(2));
    }

    @Test
    void parseComplexEntity_shouldHandleEmptyParameterPart() {
        StepEntity entity = new StepEntity(
                "#15013",
                StepEntityType.COMPLEX_ENTITY,
                "( BOUNDED_CURVE ( ) )"
        );

        ComplexEntityParser parser = new ComplexEntityParser();
        ComplexEntity result = parser.parse(entity, List.of(), Map.of());

        assertEquals("#15013", result.getId());
        assertEquals(1, result.getParts().size());

        ComplexEntityPart part = result.getParts().get(0);
        assertEquals("BOUNDED_CURVE", part.getType());
        assertEquals(0, part.getParams().size());
    }


    @Test
    void parseComplexEntity_shouldParseMixedPartsCorrectly() {
        StepEntity entity = new StepEntity(
                "#15013",
                StepEntityType.COMPLEX_ENTITY,
                "( BOUNDED_CURVE ( ) " +
                        "B_SPLINE_CURVE ( 2, ( #1, #2, #3 ), .UNSPECIFIED., .F., .F. ) " +
                        "B_SPLINE_CURVE_WITH_KNOTS ( ( 3, 3 ), ( 0.0, 1.0 ), .UNSPECIFIED. ) )"
        );

        ComplexEntityParser parser = new ComplexEntityParser();
        ComplexEntity result = parser.parse(entity, List.of(), Map.of());

        assertEquals("#15013", result.getId());
        assertEquals(3, result.getParts().size());

        ComplexEntityPart part1 = result.getParts().get(0);
        assertEquals("BOUNDED_CURVE", part1.getType());
        assertEquals(0, part1.getParams().size());

        ComplexEntityPart part2 = result.getParts().get(1);
        assertEquals("B_SPLINE_CURVE", part2.getType());
        assertEquals(5, part2.getParams().size());
        assertEquals("2", part2.getParams().get(0));
        assertEquals("( #1, #2, #3 )", part2.getParams().get(1));
        assertEquals(".UNSPECIFIED.", part2.getParams().get(2));
        assertEquals(".F.", part2.getParams().get(3));
        assertEquals(".F.", part2.getParams().get(4));

        ComplexEntityPart part3 = result.getParts().get(2);
        assertEquals("B_SPLINE_CURVE_WITH_KNOTS", part3.getType());
        assertEquals(3, part3.getParams().size());
        assertEquals("( 3, 3 )", part3.getParams().get(0));
        assertEquals("( 0.0, 1.0 )", part3.getParams().get(1));
        assertEquals(".UNSPECIFIED.", part3.getParams().get(2));
    }
}