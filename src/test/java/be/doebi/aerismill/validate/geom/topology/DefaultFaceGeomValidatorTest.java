package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultFaceGeomValidatorTest {
    DefaultFaceGeomValidator validator = new DefaultFaceGeomValidator();

    @Test
    void validate_nullFace_returnsFaceNullError() {
        ValidationReport report = validator.validate(null);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.FACE_NULL, report.messages().getFirst().code());
    }

    @Test
    void validate_faceWithoutBounds_returnsFaceNoBoundsError() {
        FaceGeom face = new FaceGeom(
                "#100",
                null,
                List.of(),
                true
        );

        ValidationReport report = validator.validate(face);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.FACE_NO_BOUNDS, report.messages().getFirst().code());
    }

    @Test
    void validate_faceWithNullBound_returnsFaceBoundNullError() {
        List<LoopGeom> bounds = new ArrayList<>();
        bounds.add(null);

        FaceGeom face = new FaceGeom(
                "#100",
                null,
                bounds,
                true
        );

        ValidationReport report = validator.validate(face);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.FACE_BOUND_NULL, report.messages().getFirst().code());
    }

    @Test
    void validate_faceWithInvalidLoop_propagatesLoopErrors() {
        LoopGeom invalidLoop = new LoopGeom(
                "#200",
                List.of()
        );

        FaceGeom face = new FaceGeom(
                "#100",
                null,
                List.of(invalidLoop),
                true
        );

        ValidationReport report = validator.validate(face);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.LOOP_EMPTY));
    }

    @Test
    void validate_faceWithValidBounds_returnsValidReport() {
        VertexGeom v1 = new VertexGeom("#1", new Point3(0.0, 0.0, 0.0));
        VertexGeom v2 = new VertexGeom("#2", new Point3(1.0, 0.0, 0.0));

        EdgeGeom edge1 = new EdgeGeom("#10", null, v1, v2, true);
        EdgeGeom edge2 = new EdgeGeom("#11", null, v2, v1, true);

        OrientedEdgeGeom oe1 = new OrientedEdgeGeom("#20", edge1, true);
        OrientedEdgeGeom oe2 = new OrientedEdgeGeom("#21", edge2, true);

        LoopGeom validLoop = new LoopGeom(
                "#200",
                List.of(oe1, oe2)
        );

        FaceGeom face = new FaceGeom(
                "#100",
                null,
                List.of(validLoop),
                true
        );

        ValidationReport report = validator.validate(face);

        assertTrue(report.isValid());
        assertFalse(report.hasErrors());
        assertTrue(report.messages().isEmpty());
    }


}