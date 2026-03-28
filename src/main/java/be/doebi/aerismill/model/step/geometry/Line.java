package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class Line extends ResolvableStepEntity {
    private final String name;
    private final String pointRef;
    private final String vectorRef;

    private CartesianPoint point;
    private Vector vector;

    public Line(String id,
                String rawParameters,
                String name,
                String pointRef,
                String vectorRef) {
        super(id, StepEntityType.LINE, rawParameters);
        this.name = name;
        this.pointRef = pointRef;
        this.vectorRef = vectorRef;
    }

    public String getName() {
        return name;
    }

    public String getPointRef() {
        return pointRef;
    }

    public String getVectorRef() {
        return vectorRef;
    }

    public CartesianPoint getPoint() {
        return point;
    }

    public Vector getVector() {
        return vector;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity pointEntity = model.getEntity(pointRef);

        if (pointEntity == null) {
            throw new StepResolveException(
                    "LINE " + getId() + " missing point reference: " + pointRef
            );
        }

        if (!(pointEntity instanceof CartesianPoint cartesianPoint)) {
            throw new StepResolveException(
                    "LINE " + getId() + " expected CARTESIAN_POINT for point " + pointRef +
                            " but found " + pointEntity.getType()
            );
        }

        this.point = cartesianPoint;

        StepEntity vectorEntity = model.getEntity(vectorRef);

        if (vectorEntity == null) {
            throw new StepResolveException(
                    "LINE " + getId() + " missing vector reference: " + vectorRef
            );
        }

        if (!(vectorEntity instanceof Vector vector)) {
            throw new StepResolveException(
                    "LINE " + getId() + " expected VECTOR for vector " + vectorRef +
                            " but found " + vectorEntity.getType()
            );
        }

        this.vector = vector;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", pointRef='" + pointRef + '\'' +
                ", vectorRef='" + vectorRef + '\'' +
                ", point=" + point +
                ", vector=" + vector +
                '}';
    }
}