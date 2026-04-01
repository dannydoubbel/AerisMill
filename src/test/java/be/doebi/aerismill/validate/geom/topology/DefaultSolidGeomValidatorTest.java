package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSolidGeomValidatorTest {

    DefaultSolidGeomValidator validator = new DefaultSolidGeomValidator();

    @Test
    void validate_nullSolid_returnsSolidNullError() {
        ValidationReport report = validator.validate(null);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.SOLID_NULL, report.messages().getFirst().code());
    }


    @Test
    void validate_solidWithNullOuterShell_returnsSolidOuterShellNullError() {
        SolidGeom solid = new SolidGeom(
                "#100",
                null
        );

        ValidationReport report = validator.validate(solid);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.SOLID_OUTER_SHELL_NULL, report.messages().getFirst().code());
    }

    @Test
    void validate_solidWithInvalidShell_propagatesShellErrors() {
        ShellGeom invalidShell = new ShellGeom(
                "#200",
                List.of()
        );

        SolidGeom solid = new SolidGeom(
                "#100",
                invalidShell
        );

        ValidationReport report = validator.validate(solid);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.SHELL_EMPTY));
    }

    @Test
    void validate_solidWithValidOuterShell_returnsValidReport() {
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

        ShellGeom validShell = new ShellGeom(
                "#150",
                List.of(validFace)
        );

        SolidGeom solid = new SolidGeom(
                "#100",
                validShell
        );

        ValidationReport report = validator.validate(solid);

        assertTrue(report.isValid());
        assertFalse(report.hasErrors());
        assertTrue(report.messages().isEmpty());
    }
}