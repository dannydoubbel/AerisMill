package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Plane;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdvancedFaceParserTest {
    @Test
    void parseAdvancedFace_shouldParseCorrectly() {
        EdgeLoop edgeLoop = new EdgeLoop(
                "#10",
                "( 'NONE', ( #60, #61 ) )",
                "NONE",
                List.of()
        );

        FaceOuterBound faceOuterBound = new FaceOuterBound(
                "#20",
                "( 'NONE', #10, .T. )",
                "NONE",
                edgeLoop,
                true
        );

        CartesianPoint point = new CartesianPoint(
                "#30",
                "( 'NONE', ( 1.0, 2.0, 3.0 ) )",
                "NONE",
                List.of(1.0, 2.0, 3.0)
        );

        Direction axis = new Direction(
                "#31",
                "( 'NONE', ( 0.0, 0.0, 1.0 ) )",
                "NONE",
                List.of(0.0, 0.0, 1.0)
        );

        Direction refDir = new Direction(
                "#32",
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(1.0, 0.0, 0.0)
        );

        Axis2Placement3D placement = new Axis2Placement3D(
                "#33",
                "( 'NONE', #30, #31, #32 )",
                "NONE",
                point,
                axis,
                refDir
        );

        Plane plane = new Plane(
                "#40",
                "( 'NONE', #33 )",
                "NONE",
                placement
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#20", faceOuterBound);
        parsedEntities.put("#40", plane);

        StepEntity entity = new StepEntity(
                "#100",
                "ADVANCED_FACE",
                "( 'NONE', ( #20 ), #40, .T. )"
        );

        List<String> params = List.of("'NONE'", "( #20 )", "#40", ".T.");

        AdvancedFaceParser parser = new AdvancedFaceParser();
        AdvancedFace result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', ( #20 ), #40, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(1, result.getBounds().size());
        assertEquals(faceOuterBound, result.getBounds().get(0));
        assertEquals(plane, result.getFaceGeometry());
        assertTrue(result.isSameSense());
    }
}