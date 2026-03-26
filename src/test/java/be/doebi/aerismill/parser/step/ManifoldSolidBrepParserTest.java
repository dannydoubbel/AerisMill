package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ManifoldSolidBrepParserTest {
    @Test
    void parseManifoldSolidBrep_shouldParseCorrectly() {
        ClosedShell closedShell = new ClosedShell(
                "#10",
                "( 'NONE', ( #20, #21 ) )",
                "NONE",
                List.of()
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", closedShell);

        StepEntity entity = new StepEntity(
                "#100",
                "MANIFOLD_SOLID_BREP",
                "( 'NONE', #10 )"
        );

        List<String> params = List.of("'NONE'", "#10");

        ManifoldSolidBrepParser parser = new ManifoldSolidBrepParser();
        ManifoldSolidBrep result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(closedShell, result.getOuter());
    }

}