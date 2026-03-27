package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

import java.util.List;

public class EdgeLoop extends TopologyEntity {
    private final String name;
    private final List<OrientedEdge> edgeList;

    public EdgeLoop(String id,
                    String rawParameters,
                    String name,
                    List<OrientedEdge> edgeList) {
        super(id, StepEntityType.EDGE_LOOP, rawParameters);
        this.name = name;
        this.edgeList = edgeList;
    }

    public String getName() {
        return name;
    }

    public List<OrientedEdge> getEdgeListRefs() {
        return edgeList;
    }

    @Override
    protected void doResolve(StepModel model) {
        // resolve refs here
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
