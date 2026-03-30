package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Ellipse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EllipseParserTest {

    private final EllipseParser parser = new EllipseParser();

    @Test
    void parse_shouldParseEllipseCorrectly() {
        String id = "#123";
        String rawParameters = "('Ellipse A',#42,25.5,10.25)";

        StepEntity result = parser.parse(id, rawParameters);

        assertNotNull(result);
        assertInstanceOf(Ellipse.class, result);

        Ellipse ellipse = (Ellipse) result;
        assertEquals("#123", ellipse.getId());
        assertEquals(StepEntityType.ELLIPSE, ellipse.getType());
        assertEquals(rawParameters, ellipse.getRawParameters());
        assertEquals("Ellipse A", ellipse.getName());
        assertEquals("#42", ellipse.getPositionRef());
        assertEquals(25.5, ellipse.getSemiAxis1());
        assertEquals(10.25, ellipse.getSemiAxis2());
        assertNull(ellipse.getPosition());
    }

    @Test
    void parse_shouldReturnNullPositionRefForDollar() {
        String id = "#124";
        String rawParameters = "('Ellipse B',$,12.0,6.0)";

        StepEntity result = parser.parse(id, rawParameters);

        assertNotNull(result);
        assertInstanceOf(Ellipse.class, result);

        Ellipse ellipse = (Ellipse) result;
        assertEquals("Ellipse B", ellipse.getName());
        assertNull(ellipse.getPositionRef());
        assertEquals(12.0, ellipse.getSemiAxis1());
        assertEquals(6.0, ellipse.getSemiAxis2());
    }

    @Test
    void parse_shouldReturnNullPositionRefForAsterisk() {
        String id = "#125";
        String rawParameters = "('Ellipse C',*,8.0,4.0)";

        StepEntity result = parser.parse(id, rawParameters);

        assertNotNull(result);
        assertInstanceOf(Ellipse.class, result);

        Ellipse ellipse = (Ellipse) result;
        assertEquals("Ellipse C", ellipse.getName());
        assertNull(ellipse.getPositionRef());
        assertEquals(8.0, ellipse.getSemiAxis1());
        assertEquals(4.0, ellipse.getSemiAxis2());
    }
}