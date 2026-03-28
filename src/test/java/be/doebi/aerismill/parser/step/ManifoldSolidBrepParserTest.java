package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ManifoldSolidBrepParserTest {
    @Test
    void parseManifoldSolidBrep_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.MANIFOLD_SOLID_BREP,
                "( 'NONE', #10 )"
        );

        List<String> params = List.of("'NONE'", "#10");

        ManifoldSolidBrepParser parser = new ManifoldSolidBrepParser();
        ManifoldSolidBrep result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#10", result.getOuterRef());
        assertNull(result.getOuter());
    }
}