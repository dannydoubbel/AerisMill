package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;

public class VertexPoint extends TopologyEntity {

    private final CartesianPoint vertexGeometry;
    private final String name;

    public VertexPoint(String id,
                       String rawParameters,
                       String name,
                       CartesianPoint vertexGeometry) {
        super(id, rawParameters, rawParameters);
        this.name = name;
        this.vertexGeometry = vertexGeometry;
    }

    public CartesianPoint getVertexGeometry() {
        return vertexGeometry;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "VertexPoint{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", vertexGeometry=" + vertexGeometry +
                '}';
    }
}
