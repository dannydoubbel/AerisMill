package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssembledSolidResultTest {

    @Test
    void constructor_acceptsSolidGeom() {
        SolidGeom solid = solidGeom("#solid1");

        AssembledSolidResult result = new AssembledSolidResult("#1", solid);

        assertEquals("#1", result.stepId());
        assertSame(solid, result.solid());
        assertTrue(result.isSolidGeom());
        assertFalse(result.isSolidWithVoidsGeom());
    }

    @Test
    void constructor_acceptsSolidWithVoidsGeom() {
        SolidWithVoidsGeom solid = solidWithVoidsGeom("#solid2");

        AssembledSolidResult result = new AssembledSolidResult("#2", solid);

        assertEquals("#2", result.stepId());
        assertSame(solid, result.solid());
        assertFalse(result.isSolidGeom());
        assertTrue(result.isSolidWithVoidsGeom());
    }

    @Test
    void constructor_rejectsNullStepId() {
        SolidGeom solid = solidGeom("#solid1");

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new AssembledSolidResult(null, solid)
        );

        assertEquals("stepId must not be null", ex.getMessage());
    }

    @Test
    void constructor_rejectsNullSolid() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new AssembledSolidResult("#1", null)
        );

        assertEquals("solid must not be null", ex.getMessage());
    }

    @Test
    void constructor_rejectsUnsupportedPayload() {
        Object unsupported = new Object();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new AssembledSolidResult("#1", unsupported)
        );

        assertTrue(ex.getMessage().contains("solid must be SolidGeom or SolidWithVoidsGeom"));
    }

    @Test
    void asSolidGeom_returnsSolidGeom_whenPresent() {
        SolidGeom solid = solidGeom("#solid1");
        AssembledSolidResult result = new AssembledSolidResult("#1", solid);

        SolidGeom extracted = result.asSolidGeom();

        assertSame(solid, extracted);
    }

    @Test
    void asSolidGeom_throws_whenPayloadIsSolidWithVoidsGeom() {
        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid2");
        AssembledSolidResult result = new AssembledSolidResult("#2", solidWithVoids);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                result::asSolidGeom
        );

        assertEquals("Result does not contain SolidGeom", ex.getMessage());
    }





    private SolidGeom solidGeom(String stepId) {
        return new SolidGeom(stepId, shell(stepId + "_outer"));
    }

    private SolidWithVoidsGeom solidWithVoidsGeom(String stepId) {
        return new SolidWithVoidsGeom(
                stepId,
                shell(stepId + "_outer"),
                List.of(shell(stepId + "_void1"))
        );
    }

    private ShellGeom shell(String stepId) {
        return new ShellGeom(stepId, List.of());
    }
}