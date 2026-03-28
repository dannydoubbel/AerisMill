package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

public class EdgeCurve extends TopologyEntity {
    private final String name;
    private final String edgeStartRef;
    private final String edgeEndRef;
    private final String edgeGeometryRef;
    private final boolean sameSense;

    private VertexPoint edgeStart;
    private VertexPoint edgeEnd;
    private StepEntity edgeGeometry;

    public EdgeCurve(String id,
                     String rawParameters,
                     String name,
                     String edgeStartRef,
                     String edgeEndRef,
                     String edgeGeometryRef,
                     boolean sameSense) {
        super(id, StepEntityType.EDGE_CURVE, rawParameters);
        this.name = name;
        this.edgeStartRef = edgeStartRef;
        this.edgeEndRef = edgeEndRef;
        this.edgeGeometryRef = edgeGeometryRef;
        this.sameSense = sameSense;
    }

    public String getName() {
        return name;
    }

    public String getEdgeStartRef() {
        return edgeStartRef;
    }

    public String getEdgeEndRef() {
        return edgeEndRef;
    }

    public String getEdgeGeometryRef() {
        return edgeGeometryRef;
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
    public void doResolve(StepModel model) {
        this.edgeStart = model.resolveEntity(edgeStartRef, VertexPoint.class);
        this.edgeEnd = model.resolveEntity(edgeEndRef, VertexPoint.class);
        this.edgeGeometry = model.resolveEntity(edgeGeometryRef, StepEntity.class);
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