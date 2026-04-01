package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultShellGeomValidatorTest {

    DefaultShellGeomValidator validator = new DefaultShellGeomValidator();

    @Test
    void validate_nullShell_returnsShellNullError() {
        ValidationReport report = validator.validate(null);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.SHELL_NULL, report.messages().getFirst().code());
    }


    @Test
    void validate_shellWithoutFaces_returnsShellEmptyError() {
        ShellGeom shell = new ShellGeom(
                "#100",
                List.of()
        );

        ValidationReport report = validator.validate(shell);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.SHELL_EMPTY, report.messages().getFirst().code());
    }

    @Test
    void validate_shellWithNullFace_returnsShellFaceNullError() {
        List<FaceGeom> faces = new ArrayList<>();
        faces.add(null);

        ShellGeom shell = new ShellGeom(
                "#100",
                faces
        );

        ValidationReport report = validator.validate(shell);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.SHELL_FACE_NULL, report.messages().getFirst().code());
    }

    @Test
    void validate_shellWithInvalidFace_propagatesFaceErrors() {
        FaceGeom invalidFace = new FaceGeom(
                "#200",
                null,
                List.of(),
                true
        );

        ShellGeom shell = new ShellGeom(
                "#100",
                List.of(invalidFace)
        );

        ValidationReport report = validator.validate(shell);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.FACE_NO_BOUNDS));
    }

    @Test
    void validate_shellWithValidFaces_returnsValidReport() {
        VertexGeom v1 = new VertexGeom("#1", new Point3(0.0, 0.0, 0.0));
        VertexGeom v2 = new VertexGeom("#2", new Point3(1.0, 0.0, 0.0));

        EdgeGeom edge1 = new EdgeGeom("#10", null, v1, v2, true);
        EdgeGeom edge2 = new EdgeGeom("#11", null, v2, v1, true);

        OrientedEdgeGeom oe1 = new OrientedEdgeGeom("#20", edge1, true);
        OrientedEdgeGeom oe2 = new OrientedEdgeGeom("#21", edge2, true);

        LoopGeom validLoop = new LoopGeom(
                "#300",
                List.of(oe1, oe2)
        );

        FaceGeom validFace = new FaceGeom(
                "#200",
                null,
                List.of(validLoop),
                true
        );

        ShellGeom shell = new ShellGeom(
                "#100",
                List.of(validFace)
        );

        ValidationReport report = validator.validate(shell);

        assertTrue(report.isValid());
        assertFalse(report.hasErrors());
        assertTrue(report.messages().isEmpty());
    }

}