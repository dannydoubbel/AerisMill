package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

public class OrientedEdge extends TopologyEntity {
    private final String name;
    private final VertexPoint edgeStart;
    private final VertexPoint edgeEnd;
    private final EdgeCurve edgeElement;
    private final boolean orientation;

    public OrientedEdge(String id,
                        String rawParameters,
                        String name,
                        VertexPoint edgeStart,
                        VertexPoint edgeEnd,
                        EdgeCurve edgeElement,
                        boolean orientation) {
        super(id, StepEntityType.ORIENTED_EDGE, rawParameters);
        this.name = name;
        this.edgeStart = edgeStart;
        this.edgeEnd = edgeEnd;
        this.edgeElement = edgeElement;
        this.orientation = orientation;
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

    public EdgeCurve getEdgeElement() {
        return edgeElement;
    }

    public boolean isOrientation() {
        return orientation;
    }

    @Override
    protected void doResolve(StepModel model) {
        // resolve refs here
    }

    @Override
    public String toString() {
        return "OrientedEdge{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", edgeStart=" + edgeStart +
                ", edgeEnd=" + edgeEnd +
                ", edgeElement=" + edgeElement +
                ", orientation=" + orientation +
                '}';
    }
}
