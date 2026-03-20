package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;

import java.util.List;

public class ClosedShell extends TopologyEntity {
    private final String name;
    private final List<AdvancedFace> cfsFaces;

    public ClosedShell(String id,
                       String rawParameters,
                       String name,
                       List<AdvancedFace> cfsFaces) {
        super(id, "CLOSED_SHELL", rawParameters);
        this.name = name;
        this.cfsFaces = cfsFaces;
    }

    public String getName() {
        return name;
    }

    public List<AdvancedFace> getCfsFaces() {
        return cfsFaces;
    }

    @Override
    public String toString() {
        return "ClosedShell{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", cfsFaces=" + cfsFaces +
                '}';
    }
}
