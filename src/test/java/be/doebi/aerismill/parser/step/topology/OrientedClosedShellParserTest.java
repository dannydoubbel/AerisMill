package be.doebi.aerismill.parser.step.topology;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.OrientedClosedShell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrientedClosedShellParserTest {

    private final OrientedClosedShellParser parser = new OrientedClosedShellParser();

    @Test
    void parse_shouldParseOrientedClosedShellCorrectly_whenOrientationTrue() {
        String id = "#70422";
        String rawParameters = "('NONE',*,#123,.T.)";

        StepEntity result = parser.parse(id, rawParameters);

        assertNotNull(result);
        assertInstanceOf(OrientedClosedShell.class, result);

        OrientedClosedShell shell = (OrientedClosedShell) result;
        assertEquals("#70422", shell.getId());
        assertEquals(StepEntityType.ORIENTED_CLOSED_SHELL, shell.getType());
        assertEquals(rawParameters, shell.getRawParameters());
        assertEquals("NONE", shell.getName());
        assertEquals("#123", shell.getClosedShellRef());
        assertTrue(shell.isOrientation());
        assertNull(shell.getClosedShell());
    }

    @Test
    void parse_shouldParseOrientedClosedShellCorrectly_whenOrientationFalse() {
        String id = "#70423";
        String rawParameters = "('NONE',*,#456,.F.)";

        StepEntity result = parser.parse(id, rawParameters);

        assertNotNull(result);
        assertInstanceOf(OrientedClosedShell.class, result);

        OrientedClosedShell shell = (OrientedClosedShell) result;
        assertEquals("NONE", shell.getName());
        assertEquals("#456", shell.getClosedShellRef());
        assertFalse(shell.isOrientation());
        assertNull(shell.getClosedShell());
    }
}