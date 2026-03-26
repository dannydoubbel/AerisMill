package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntity;


public class EdgeCurve extends TopologyEntity {
    private final String name;
    private final VertexPoint edgeStart;
    private final VertexPoint edgeEnd;
    private final StepEntity edgeGeometry;
    private final boolean sameSense;

    public EdgeCurve(String id,
                     String rawParameters,
                     String name,
                     VertexPoint edgeStart,
                     VertexPoint edgeEnd,
                     StepEntity edgeGeometry,
                     boolean sameSense) {
        super(id, "EDGE_CURVE", rawParameters);
        this.name = name;
        this.edgeStart = edgeStart;
        this.edgeEnd = edgeEnd;
        this.edgeGeometry = edgeGeometry;
        this.sameSense = sameSense;
    }

    public String getName() {
        return name;
    }

    public VertexPoint getEdgeStart() {
        return edgeStart;
    }

    public VertexPoint getEdgeEnd() {
        return edgeEnd;
    }

    public StepEntity getEdgeGeometry() {
        return edgeGeometry;
    }

    public boolean isSameSense() {
        return sameSense;
    }

    @Override
    public String toString() {
        return "EdgeCurve{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", edgeStart=" + edgeStart +
                ", edgeEnd=" + edgeEnd +
                ", edgeGeometry=" + edgeGeometry +
                ", sameSense=" + sameSense +
                '}';
    }
}
