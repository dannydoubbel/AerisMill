package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

import java.util.ArrayList;
import java.util.List;

public class BrepWithVoids extends TopologyEntity {
    private final String name;
    private final String outerRef;
    private final List<String> voidRefs;

    private ClosedShell outer;
    private List<ClosedShell> voids;

    public BrepWithVoids(String id,
                         String rawParameters,
                         String name,
                         String outerRef,
                         List<String> voidRefs) {
        super(id, StepEntityType.BREP_WITH_VOIDS, rawParameters);
        this.name = name;
        this.outerRef = outerRef;
        this.voidRefs = voidRefs;
    }

    public String getName() {
        return name;
    }

    public String getOuterRef() {
        return outerRef;
    }

    public List<String> getVoidRefs() {
        return voidRefs;
    }

    public ClosedShell getOuter() {
        return outer;
    }

    public List<ClosedShell> getVoids() {
        return voids;
    }

    @Override
    public void doResolve(StepModel model) {
        this.outer = outerRef == null ? null : resolveClosedShellLike(model, outerRef);
        this.voids = resolveClosedShellLikeList(model, voidRefs);
    }

    private ClosedShell resolveClosedShellLike(StepModel model, String ref) {
        StepEntity entity = model.resolveEntity(ref, StepEntity.class);

        if (entity instanceof ClosedShell closedShell) {
            return closedShell;
        }

        if (entity instanceof OrientedClosedShell orientedClosedShell) {
            ClosedShell resolved = orientedClosedShell.getClosedShell();
            if (resolved == null) {
                throw new StepResolveException(
                        "ORIENTED_CLOSED_SHELL " + orientedClosedShell.getId()
                                + " did not resolve to CLOSED_SHELL"
                );
            }
            return resolved;
        }

        throw new StepResolveException(
                "Referenced entity " + ref + " is "
                        + (entity == null ? "null" : entity.getClass().getSimpleName())
                        + ", expected ClosedShell or OrientedClosedShell"
        );
    }

    private List<ClosedShell> resolveClosedShellLikeList(StepModel model, List<String> refs) {
        List<ClosedShell> resolved = new ArrayList<>();

        for (String ref : refs) {
            resolved.add(resolveClosedShellLike(model, ref));
        }

        return resolved;
    }

    @Override
    public String toString() {
        return "BrepWithVoids{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", outerRef='" + outerRef + '\'' +
                ", voidRefs=" + voidRefs +
                ", outer=" + outer +
                ", voids=" + voids +
                '}';
    }
}