package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTopologyValidationServiceTest {
    TopologyValidationService service = new DefaultTopologyValidationService();

    @Test
    void validateFace_delegatesToFaceValidator() {
        FaceGeom face = new FaceGeom(
                "#100",
                null,
                List.of(),
                true
        );

        ValidationReport report = service.validateFace(face);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.FACE_NO_BOUNDS));
    }

    @Test
    void validateShell_delegatesToShellValidator() {
        ShellGeom shell = new ShellGeom(
                "#100",
                List.of()
        );

        ValidationReport report = service.validateShell(shell);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.SHELL_EMPTY));
    }

    @Test
    void validateSolid_delegatesToSolidValidator() {
        SolidGeom solid = new SolidGeom(
                "#100",
                null
        );

        ValidationReport report = service.validateSolid(solid);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.SOLID_OUTER_SHELL_NULL));
    }

    @Test
    void validateLoop_delegatesToLoopValidator() {
        LoopGeom loop = new LoopGeom(
                "#100",
                List.of()
        );

        ValidationReport report = service.validateLoop(loop);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.LOOP_EMPTY));
    }

}