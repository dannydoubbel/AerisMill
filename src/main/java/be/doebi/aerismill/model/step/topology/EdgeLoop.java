package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

import java.util.List;

public class EdgeLoop extends ResolvableStepEntity {
    private final String name;
    private final List<String> edgeRefs;

    private List<OrientedEdge> edgeList;

    public EdgeLoop(String id,
                    String rawParameters,
                    String name,
                    List<String> edgeRefs) {
        super(id, StepEntityType.EDGE_LOOP, rawParameters);
        this.name = name;
        this.edgeRefs = edgeRefs;
    }

    public String getName() {
        return name;
    }

    public List<String> getEdgeRefs() {
        return edgeRefs;
    }

    public List<OrientedEdge> getEdgeList() {
        return edgeList;
    }

    @Override
    public void doResolve(StepModel model) {
        this.edgeList = model.resolveEntityList(edgeRefs, OrientedEdge.class);
    }

    @Override
    public String toString() {
        return "EdgeLoop{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", edgeRefs=" + edgeRefs +
                ", edgeList=" + edgeList +
                '}';
    }
}