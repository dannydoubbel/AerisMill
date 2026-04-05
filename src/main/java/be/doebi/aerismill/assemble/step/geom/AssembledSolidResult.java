package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;

import java.util.Objects;

public record AssembledSolidResult(
        String stepId,
        Object solid
) {
    public AssembledSolidResult {
        Objects.requireNonNull(stepId, "stepId must not be null");
        Objects.requireNonNull(solid, "solid must not be null");

        if (!(solid instanceof SolidGeom) && !(solid instanceof SolidWithVoidsGeom)) {
            throw new IllegalArgumentException(
                    "solid must be SolidGeom or SolidWithVoidsGeom, got: " + solid.getClass().getName()
            );
        }
    }

    public boolean isSolidGeom() {
        return solid instanceof SolidGeom;
    }

    public boolean isSolidWithVoidsGeom() {
        return solid instanceof SolidWithVoidsGeom;
    }

    public SolidGeom asSolidGeom() {
        if (!(solid instanceof SolidGeom solidGeom)) {
            throw new IllegalStateException("Result does not contain SolidGeom");
        }
        return solidGeom;
    }

    public SolidWithVoidsGeom asSolidWithVoidsGeom() {
        if (!(solid instanceof SolidWithVoidsGeom solidWithVoidsGeom)) {
            throw new IllegalStateException("Result does not contain SolidWithVoidsGeom");
        }
        return solidWithVoidsGeom;
    }
}