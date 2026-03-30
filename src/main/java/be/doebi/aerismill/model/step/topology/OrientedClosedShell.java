package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class OrientedClosedShell extends TopologyEntity {
    private final String name;
    private final String closedShellRef;
    private final boolean orientation;

    private ClosedShell closedShell;

    public OrientedClosedShell(String id,
                               String rawParameters,
                               String name,
                               String closedShellRef,
                               boolean orientation) {
        super(id, StepEntityType.ORIENTED_CLOSED_SHELL, rawParameters);
        this.name = name;
        this.closedShellRef = closedShellRef;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public String getClosedShellRef() {
        return closedShellRef;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public ClosedShell getClosedShell() {
        return closedShell;
    }

    @Override
    protected void doResolve(StepModel model) {
        StepEntity entity = model.getEntity(closedShellRef);

        if (entity == null) {
            throw new StepResolveException(
                    "ORIENTED_CLOSED_SHELL " + getId() +
                            " missing closed shell reference: " + closedShellRef
            );
        }

        if (!(entity instanceof ClosedShell resolvedClosedShell)) {
            throw new StepResolveException(
                    "ORIENTED_CLOSED_SHELL " + getId() +
                            " expected CLOSED_SHELL for reference " + closedShellRef +
                            " but found " + entity.getType()
            );
        }

        this.closedShell = resolvedClosedShell;
    }

    @Override
    public String toString() {
        return "OrientedClosedShell{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", closedShellRef='" + closedShellRef + '\'' +
                ", orientation=" + orientation +
                ", closedShell=" + closedShell +
                '}';
    }
}