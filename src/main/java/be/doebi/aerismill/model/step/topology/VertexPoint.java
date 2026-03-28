package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;

public class VertexPoint extends TopologyEntity {

    private  CartesianPoint vertexGeometry;
    private final String name;
    private final String vertexGeometryRef;

    public VertexPoint(String id,
                       String rawParameters,
                       String name,
                       String vertexGeometryRef) {
        super(id, StepEntityType.VERTEX_POINT, rawParameters);
        this.name = name;
        this.vertexGeometryRef = vertexGeometryRef;
    }

    public CartesianPoint getVertexGeometry() {
        return vertexGeometry;
    }

    public String getVertexGeometryRef() {
        return vertexGeometryRef;
    }

    public String getName() {
        return name;
    }

    @Override
    public void doResolve(StepModel model) {
        this.vertexGeometry = model.resolveEntity(vertexGeometryRef, CartesianPoint.class);
    }

    @Override
    public String toString() {
        return "VertexPoint{" +
                "id='" + getId() + '\'' +
                ", type=" + getType() +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", vertexGeometry=" + vertexGeometry +
                '}';
    }
}