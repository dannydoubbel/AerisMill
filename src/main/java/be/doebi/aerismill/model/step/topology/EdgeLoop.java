package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;

import java.util.List;

public class EdgeLoop extends TopologyEntity {
    private final String name;
    private final List<OrientedEdge> edgeList;

    public EdgeLoop(String id,
                    String rawParameters,
                    String name,
                    List<OrientedEdge> edgeList) {
        super(id, "EDGE_LOOP", rawParameters);
        this.name = name;
        this.edgeList = edgeList;
    }

    public String getName() {
        return name;
    }

    public List<OrientedEdge> getEdgeList() {
        return edgeList;
    }

    @Override
    public String toString() {
        return "EdgeLoop{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", edgeList=" + edgeList +
                '}';
    }
}
