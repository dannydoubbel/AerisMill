package be.doebi.aerismill.eval.step.representation;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.topology.ManifoldSolidBrepEvaluator;
import be.doebi.aerismill.eval.step.topology.SolidWithVoidsGeomEvaluator;
import be.doebi.aerismill.model.geom.representation.EvaluatedBrepRepresentation;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DefaultAdvancedBrepShapeRepresentationEvaluator
        implements AdvancedBrepShapeRepresentationEvaluator {

    private final StepEvaluationContext context;
    private final ManifoldSolidBrepEvaluator manifoldSolidBrepEvaluator;
    private final SolidWithVoidsGeomEvaluator solidWithVoidsGeomEvaluator;

    public DefaultAdvancedBrepShapeRepresentationEvaluator(
            StepEvaluationContext context,
            ManifoldSolidBrepEvaluator manifoldSolidBrepEvaluator,
            SolidWithVoidsGeomEvaluator solidWithVoidsGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.manifoldSolidBrepEvaluator = Objects.requireNonNull(
                manifoldSolidBrepEvaluator, "manifoldSolidBrepEvaluator must not be null"
        );
        this.solidWithVoidsGeomEvaluator = Objects.requireNonNull(
                solidWithVoidsGeomEvaluator, "solidWithVoidsGeomEvaluator must not be null"
        );
    }

    @Override
    public EvaluatedBrepRepresentation evaluateRepresentation(AdvancedBrepShapeRepresentation representation) {
        Objects.requireNonNull(representation, "representation must not be null");

        List<StepEntity> items = representation.getItems();
        if (items == null) {
            items = requireItems(representation);
        }

        List<SolidGeom> manifoldSolids = new ArrayList<>();
        List<SolidWithVoidsGeom> solidsWithVoids = new ArrayList<>();

        for (StepEntity item : items) {
            if (item instanceof ManifoldSolidBrep manifoldSolidBrep) {
                manifoldSolids.add(manifoldSolidBrepEvaluator.evaluateSolid(manifoldSolidBrep));
            } else if (item instanceof BrepWithVoids brepWithVoids) {
                solidsWithVoids.add(solidWithVoidsGeomEvaluator.evaluateSolid(brepWithVoids));
            }
        }

        return new EvaluatedBrepRepresentation(
                representation.getId(),
                List.copyOf(manifoldSolids),
                List.copyOf(solidsWithVoids)
        );
    }

    private List<StepEntity> requireItems(AdvancedBrepShapeRepresentation representation) {
        return representation.getItemRefs().stream()
                .map(ref -> requireEntity(ref, representation.getId()))
                .toList();
    }

    private StepEntity requireEntity(String ref, String ownerId) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (entity == null) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected item ref " + ref
            );
        }
        return entity;
    }
}