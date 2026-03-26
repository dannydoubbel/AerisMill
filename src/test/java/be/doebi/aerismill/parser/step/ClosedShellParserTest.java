package be.doebi.aerismill.parser.step;

import static org.junit.jupiter.api.Assertions.*;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ClosedShellParserTest {
    @Test
    void parseClosedShell_shouldParseCorrectly() {
        AdvancedFace face1 = new AdvancedFace(
                "#10",
                "( 'NONE', ( #20 ), #40, .T. )",
                "NONE",
                List.of(),
                null,
                true
        );

        AdvancedFace face2 = new AdvancedFace(
                "#11",
                "( 'NONE', ( #21 ), #41, .T. )",
                "NONE",
                List.of(),
                null,
                true
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", face1);
        parsedEntities.put("#11", face2);

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.CLOSED_SHELL,
                "( 'NONE', ( #10, #11 ) )"
        );

        List<String> params = List.of("'NONE'", "( #10, #11 )");

        ClosedShellParser parser = new ClosedShellParser();
        ClosedShell result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', ( #10, #11 ) )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(2, result.getCfsFaces().size());
        assertEquals(face1, result.getCfsFaces().get(0));
        assertEquals(face2, result.getCfsFaces().get(1));
    }
}