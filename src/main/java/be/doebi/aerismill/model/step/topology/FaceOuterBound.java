package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;

public class FaceOuterBound extends TopologyEntity {
    private final String name;
    private final EdgeLoop bound;
    private final boolean orientation;

    public FaceOuterBound(String id,
                          String rawParameters,
                          String name,
                          EdgeLoop bound,
                          boolean orientation) {
        super(id, "FACE_OUTER_BOUND", rawParameters);
        this.name = name;
        this.bound = bound;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public EdgeLoop getBound() {
        return bound;
    }

    public boolean isOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "FaceOuterBound{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", bound=" + bound +
                ", orientation=" + orientation +
                '}';
    }
}
