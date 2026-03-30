package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class Ellipse extends ResolvableStepEntity {
    private final String name;
    private final String positionRef;
    private final double semiAxis1;
    private final double semiAxis2;

    private Axis2Placement3D position;

    public Ellipse(String id,
                   String rawParameters,
                   String name,
                   String positionRef,
                   double semiAxis1,
                   double semiAxis2) {
        super(id, StepEntityType.ELLIPSE, rawParameters);
        this.name = name;
        this.positionRef = positionRef;
        this.semiAxis1 = semiAxis1;
        this.semiAxis2 = semiAxis2;
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

    public double getSemiAxis1() {
        return semiAxis1;
    }

    public double getSemiAxis2() {
        return semiAxis2;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity entity = model.getEntity(positionRef);

        if (entity == null) {
            throw new StepResolveException(
                    "ELLIPSE " + getId() + " missing position reference: " + positionRef
            );
        }

        if (!(entity instanceof Axis2Placement3D axis2Placement3D)) {
            throw new StepResolveException(
                    "ELLIPSE " + getId() +
                            " expected AXIS2_PLACEMENT_3D for position " + positionRef +
                            " but found " + entity.getType()
            );
        }

        this.position = axis2Placement3D;
    }

    @Override
    public String toString() {
        return "Ellipse{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", positionRef='" + positionRef + '\'' +
                ", position=" + position +
                ", semiAxis1=" + semiAxis1 +
                ", semiAxis2=" + semiAxis2 +
                '}';
    }
}