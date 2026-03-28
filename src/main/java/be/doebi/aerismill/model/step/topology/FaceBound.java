package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class FaceBound extends ResolvableStepEntity {
    private final String name;
    private final String boundRef;
    private final boolean orientation;

    private EdgeLoop bound;

    public FaceBound(
            String id,
            String rawParameters,
            String name,
            String boundRef,
            boolean orientation
    ) {
        super(id, StepEntityType.FACE_BOUND, rawParameters);
        this.name = name;
        this.boundRef = boundRef;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public String getBoundRef() {
        return boundRef;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public EdgeLoop getBound() {
        return bound;
    }


    @Override
    public void doResolve(StepModel model) {
        StepEntity entity = model.getEntity(boundRef);

        if (entity == null) {
            throw new StepResolveException(
                    "FACE_BOUND " + getId() + " missing bound reference: " + boundRef
            );
        }

        if (!(entity instanceof EdgeLoop edgeLoop)) {
            throw new StepResolveException(
                    "FACE_BOUND " + getId() + " expected EDGE_LOOP for bound " + boundRef +
                            " but found " + entity.getType()
            );
        }

        this.bound = edgeLoop;
    }
}