package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

import java.util.ArrayList;
import java.util.List;

public class BSplineSurfaceWithKnots extends ResolvableStepEntity {
    private final String name;
    private final int uDegree;
    private final int vDegree;
    private final List<List<String>> controlPointRefs;
    private final String surfaceForm;
    private final StepLogical uClosed;
    private final StepLogical vClosed;
    private final StepLogical selfIntersect;
    private final List<Integer> uMultiplicities;
    private final List<Integer> vMultiplicities;
    private final List<Double> uKnots;
    private final List<Double> vKnots;
    private final String knotSpec;
    private final List<List<Double>> weights;

    private final List<List<StepEntity>> controlPointsList;

    public BSplineSurfaceWithKnots(
            String id,
            String rawParameters,
            String name,
            int uDegree,
            int vDegree,
            List<List<String>> controlPointRefs,
            String surfaceForm,
            StepLogical uClosed,
            StepLogical vClosed,
            StepLogical selfIntersect,
            List<Integer> uMultiplicities,
            List<Integer> vMultiplicities,
            List<Double> uKnots,
            List<Double> vKnots,
            String knotSpec
    ) {
        this(
                id,
                rawParameters,
                name,
                uDegree,
                vDegree,
                controlPointRefs,
                surfaceForm,
                uClosed,
                vClosed,
                selfIntersect,
                uMultiplicities,
                vMultiplicities,
                uKnots,
                vKnots,
                knotSpec,
                null
        );
    }

    public BSplineSurfaceWithKnots(
            String id,
            String rawParameters,
            String name,
            int uDegree,
            int vDegree,
            List<List<String>> controlPointRefs,
            String surfaceForm,
            StepLogical uClosed,
            StepLogical vClosed,
            StepLogical selfIntersect,
            List<Integer> uMultiplicities,
            List<Integer> vMultiplicities,
            List<Double> uKnots,
            List<Double> vKnots,
            String knotSpec,
            List<List<Double>> weights
    ) {
        super(id, StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS, rawParameters);
        this.name = name;
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
        this.weights = weights == null ? null : copyDoubleGrid(weights);
        this.controlPointsList = new ArrayList<>();
    }
    public String getName() {        return name;    }

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

    public StepLogical isUClosed() {
        return uClosed;
    }

    public StepLogical isVClosed() {
        return vClosed;
    }

    public StepLogical isSelfIntersect() {
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

    public List<List<Double>> getWeights() {
        return weights;
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
                ", weights=" + weights +
                ", knotSpec='" + knotSpec + '\'' +
                '}';
    }

    private List<List<Double>> copyDoubleGrid(List<List<Double>> source) {
        List<List<Double>> copy = new ArrayList<>(source.size());
        for (List<Double> row : source) {
            copy.add(List.copyOf(row));
        }
        return List.copyOf(copy);
    }
}
