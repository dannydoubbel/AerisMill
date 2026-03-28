package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class Circle extends ResolvableStepEntity {
    private final String name;
    private final String positionRef;
    private final double radius;

    private Axis2Placement3D position;

    public Circle(String id,
                  String rawParameters,
                  String name,
                  String positionRef,
                  double radius) {
        super(id, StepEntityType.CIRCLE, rawParameters);
        this.name = name;
        this.positionRef = positionRef;
        this.radius = radius;
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

    public double getRadius() {
        return radius;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity entity = model.getEntity(positionRef);

        if (entity == null) {
            throw new StepResolveException(
                    "CIRCLE " + getId() + " missing position reference: " + positionRef
            );
        }

        if (!(entity instanceof Axis2Placement3D axis2Placement3D)) {
            throw new StepResolveException(
                    "CIRCLE " + getId() +
                            " expected AXIS2_PLACEMENT_3D for position " + positionRef +
                            " but found " + entity.getType()
            );
        }

        this.position = axis2Placement3D;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", positionRef='" + positionRef + '\'' +
                ", position=" + position +
                ", radius=" + radius +
                '}';
    }
}