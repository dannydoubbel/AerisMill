package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class SurfaceOfRevolution extends ResolvableStepEntity {
    private final String name;
    private final String sweptCurveRef;
    private final String axisPositionRef;

    private StepEntity sweptCurve;
    private Axis1Placement axisPosition;

    public SurfaceOfRevolution(String id,
                               String rawParameters,
                               String name,
                               String sweptCurveRef,
                               String axisPositionRef) {
        super(id, StepEntityType.SURFACE_OF_REVOLUTION, rawParameters);
        this.name = name;
        this.sweptCurveRef = sweptCurveRef;
        this.axisPositionRef = axisPositionRef;
    }

    public String getName() {
        return name;
    }

    public String getSweptCurveRef() {
        return sweptCurveRef;
    }

    public String getAxisPositionRef() {
        return axisPositionRef;
    }

    public StepEntity getSweptCurve() {
        return sweptCurve;
    }

    public Axis1Placement getAxisPosition() {
        return axisPosition;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity sweptCurveEntity = model.getEntity(sweptCurveRef);
        if (sweptCurveEntity == null) {
            throw new StepResolveException(
                    "SURFACE_OF_REVOLUTION " + getId() +
                            " missing swept curve reference: " + sweptCurveRef
            );
        }

        StepEntity axisEntity = model.getEntity(axisPositionRef);
        if (axisEntity == null) {
            throw new StepResolveException(
                    "SURFACE_OF_REVOLUTION " + getId() +
                            " missing axis position reference: " + axisPositionRef
            );
        }

        if (!(axisEntity instanceof Axis1Placement axis1Placement)) {
            throw new StepResolveException(
                    "SURFACE_OF_REVOLUTION " + getId() +
                            " expected AXIS1_PLACEMENT for axis position " + axisPositionRef +
                            " but found " + axisEntity.getType()
            );
        }

        this.sweptCurve = sweptCurveEntity;
        this.axisPosition = axis1Placement;
    }

    @Override
    public String toString() {
        return "SurfaceOfRevolution{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", sweptCurveRef='" + sweptCurveRef + '\'' +
                ", axisPositionRef='" + axisPositionRef + '\'' +
                ", sweptCurve=" + sweptCurve +
                ", axisPosition=" + axisPosition +
                '}';
    }
}