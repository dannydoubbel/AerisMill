package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

public class OrientedEdge extends TopologyEntity {
    private final String name;
    private final String edgeStartRef;
    private final String edgeEndRef;
    private final String edgeElementRef;
    private final boolean orientation;

    private VertexPoint edgeStart;
    private VertexPoint edgeEnd;
    private EdgeCurve edgeElement;

    public OrientedEdge(String id,
                        String rawParameters,
                        String name,
                        String edgeStartRef,
                        String edgeEndRef,
                        String edgeElementRef,
                        boolean orientation) {
        super(id, StepEntityType.ORIENTED_EDGE, rawParameters);
        this.name = name;
        this.edgeStartRef = edgeStartRef;
        this.edgeEndRef = edgeEndRef;
        this.edgeElementRef = edgeElementRef;
        this.orientation = orientation;
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

    public String getEdgeElementRef() {
        return edgeElementRef;
    }

    public VertexPoint getEdgeStart() {
        return edgeStart;
    }

    public VertexPoint getEdgeEnd() {
        return edgeEnd;
    }

    public EdgeCurve getEdgeElement() {
        return edgeElement;
    }

    public boolean isOrientation() {
        return orientation;
    }

    @Override
    public void doResolve(StepModel model) {
        this.edgeStart = edgeStartRef == null ? null : model.resolveEntity(edgeStartRef, VertexPoint.class);
        this.edgeEnd = edgeEndRef == null ? null : model.resolveEntity(edgeEndRef, VertexPoint.class);
        this.edgeElement = edgeElementRef == null ? null : model.resolveEntity(edgeElementRef, EdgeCurve.class);
    }

    @Override
    public String toString() {
        return "OrientedEdge{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", edgeStartRef='" + edgeStartRef + '\'' +
                ", edgeEndRef='" + edgeEndRef + '\'' +
                ", edgeElementRef='" + edgeElementRef + '\'' +
                ", edgeStart=" + edgeStart +
                ", edgeEnd=" + edgeEnd +
                ", edgeElement=" + edgeElement +
                ", orientation=" + orientation +
                '}';
    }
}