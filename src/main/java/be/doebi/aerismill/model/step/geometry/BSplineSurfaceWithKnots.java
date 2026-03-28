package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

import java.util.ArrayList;
import java.util.List;

public class BSplineSurfaceWithKnots extends ResolvableStepEntity {
    private final int uDegree;
    private final int vDegree;
    private final List<List<String>> controlPointRefs;
    private final String surfaceForm;
    private final boolean uClosed;
    private final boolean vClosed;
    private final boolean selfIntersect;
    private final List<Integer> uMultiplicities;
    private final List<Integer> vMultiplicities;
    private final List<Double> uKnots;
    private final List<Double> vKnots;
    private final String knotSpec;

    private final List<List<StepEntity>> controlPointsList;

    public BSplineSurfaceWithKnots(
            String id,
            String rawParameters,
            int uDegree,
            int vDegree,
            List<List<String>> controlPointRefs,
            String surfaceForm,
            boolean uClosed,
            boolean vClosed,
            boolean selfIntersect,
            List<Integer> uMultiplicities,
            List<Integer> vMultiplicities,
            List<Double> uKnots,
            List<Double> vKnots,
            String knotSpec
    ) {
        super(id, StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS, rawParameters);
        this.uDegree = uDegree;
        this.vDegree = vDegree;
        this.controlPointRefs = controlPointRefs;
        this.surfaceForm = surfaceForm;
        this.uClosed = uClosed;
        this.vClosed = vClosed;
        this.selfIntersect = selfIntersect;
        this.uMultiplicities = uMultiplicities;
        this.vMultiplicities = vMultiplicities;
        this.uKnots = uKnots;
        this.vKnots = vKnots;
        this.knotSpec = knotSpec;
        this.controlPointsList = new ArrayList<>();
    }

    public int getUDegree() {
        return uDegree;
    }

    public int getVDegree() {
        return vDegree;
    }

    public List<List<String>> getControlPointRefs() {
        return controlPointRefs;
    }

    public List<List<StepEntity>> getControlPointsList() {
        return controlPointsList;
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public boolean isUClosed() {
        return uClosed;
    }

    public boolean isVClosed() {
        return vClosed;
    }

    public boolean isSelfIntersect() {
        return selfIntersect;
    }

    public List<Integer> getUMultiplicities() {
        return uMultiplicities;
    }

    public List<Integer> getVMultiplicities() {
        return vMultiplicities;
    }

    public List<Double> getUKnots() {
        return uKnots;
    }

    public List<Double> getVKnots() {
        return vKnots;
    }

    public String getKnotSpec() {
        return knotSpec;
    }

    @Override
    public void doResolve(StepModel model) {
        controlPointsList.clear();

        for (List<String> rowRefs : controlPointRefs) {
            List<StepEntity> resolvedRow = new ArrayList<>();

            for (String ref : rowRefs) {
                StepEntity entity = model.getEntity(ref);

                if (entity == null) {
                    throw new StepResolveException(
                            "B_SPLINE_SURFACE_WITH_KNOTS " + getId() +
                                    " missing control point reference: " + ref
                    );
                }

                resolvedRow.add(entity);
            }

            controlPointsList.add(resolvedRow);
        }
    }

    @Override
    public String toString() {
        return "BSplineSurfaceWithKnots{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", uDegree=" + uDegree +
                ", vDegree=" + vDegree +
                ", controlPointRefs=" + controlPointRefs +
                ", controlPointsList=" + controlPointsList +
                ", surfaceForm='" + surfaceForm + '\'' +
                ", uClosed=" + uClosed +
                ", vClosed=" + vClosed +
                ", selfIntersect=" + selfIntersect +
                ", uMultiplicities=" + uMultiplicities +
                ", vMultiplicities=" + vMultiplicities +
                ", uKnots=" + uKnots +
                ", vKnots=" + vKnots +
                ", knotSpec='" + knotSpec + '\'' +
                '}';
    }
}