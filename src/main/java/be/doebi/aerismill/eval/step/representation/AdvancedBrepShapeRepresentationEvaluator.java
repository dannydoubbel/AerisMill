package be.doebi.aerismill.eval.step.representation;

import be.doebi.aerismill.model.geom.representation.EvaluatedBrepRepresentation;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;

public interface AdvancedBrepShapeRepresentationEvaluator {
    EvaluatedBrepRepresentation evaluateRepresentation(AdvancedBrepShapeRepresentation representation);
}