package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class ToroidalSurface extends ResolvableStepEntity {
    private final String name;
    private final String positionRef;
    private final double majorRadius;
    private final double minorRadius;

    private Axis2Placement3D position;

    public ToroidalSurface(String id,
                           String rawParameters,
                           String name,
                           String positionRef,
                           double majorRadius,
                           double minorRadius) {
        super(id, StepEntityType.TOROIDAL_SURFACE, rawParameters);
        this.name = name;
        this.positionRef = positionRef;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
    }

    public String getName() {
        return name;
    }

    public String getPositionRef() {
        return positionRef;
    }

    public Axis2Placement3D getPosition() {
        return position;
    }

    public double getMajorRadius() {
        return majorRadius;
    }

    public double getMinorRadius() {
        return minorRadius;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity entity = model.getEntity(positionRef);

        if (entity == null) {
            throw new StepResolveException(
                    "TOROIDAL_SURFACE " + getId() + " missing position reference: " + positionRef
            );
        }

        if (!(entity instanceof Axis2Placement3D axis2Placement3D)) {
            throw new StepResolveException(
                    "TOROIDAL_SURFACE " + getId() +
                            " expected AXIS2_PLACEMENT_3D for position " + positionRef +
                            " but found " + entity.getType()
            );
        }

        this.position = axis2Placement3D;
    }

    @Override
    public String toString() {
        return "ToroidalSurface{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", positionRef='" + positionRef + '\'' +
                ", position=" + position +
                ", majorRadius=" + majorRadius +
                ", minorRadius=" + minorRadius +
                '}';
    }
}