package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;

public class DefaultLoopGeomValidator {

    public ValidationReport validate(LoopGeom loop) {
        ValidationReport report = new ValidationReport();

        if (loop == null) {
            report.addError(
                    ValidationCode.LOOP_NULL,
                    null,
                    "LoopGeom is null"
            );
            return report;
        }

        if (loop.edges() == null || loop.edges().isEmpty()) {
            report.addError(
                    ValidationCode.LOOP_EMPTY,
                    loop.stepId(),
                    "LoopGeom must contain at least one oriented edge"
            );
            return report;
        }

        for (int i = 0; i < loop.edges().size(); i++) {
            OrientedEdgeGeom orientedEdge = loop.edges().get(i);

            if (orientedEdge == null) {
                report.addError(
                        ValidationCode.LOOP_EDGE_NULL,
                        loop.stepId(),
                        "LoopGeom contains a null oriented edge at index " + i
                );
                continue;
            }

            if (orientedEdge.edge() == null) {
                report.addError(
                        ValidationCode.LOOP_EDGE_NULL,
                        loop.stepId(),
                        "LoopGeom contains an oriented edge with null edge at index " + i
                );
                continue;
            }

            if (orientedEdge.edge().edgeStart() == null) {
                report.addError(
                        ValidationCode.LOOP_EDGE_START_NULL,
                        loop.stepId(),
                        "LoopGeom contains an oriented edge with null edgeStart vertex at index " + i
                );
            } else if (orientedEdge.edge().edgeStart().position() == null) {
                report.addError(
                        ValidationCode.LOOP_EDGE_START_NULL,
                        loop.stepId(),
                        "LoopGeom contains an oriented edge with null start position at index " + i
                );
            }

            if (orientedEdge.edge().edgeEnd() == null) {
                report.addError(
                        ValidationCode.LOOP_EDGE_END_NULL,
                        loop.stepId(),
                        "LoopGeom contains an oriented edge with null edgeEnd vertex at index " + i
                );
            } else if (orientedEdge.edge().edgeEnd().position() == null) {
                report.addError(
                        ValidationCode.LOOP_EDGE_END_NULL,
                        loop.stepId(),
                        "LoopGeom contains an oriented edge with null end position at index " + i
                );
            }
        }

        if (report.hasErrors()) {
            return report;
        }

        for (int i = 0; i < loop.edges().size() - 1; i++) {
            OrientedEdgeGeom current = loop.edges().get(i);
            OrientedEdgeGeom next = loop.edges().get(i + 1);

            if (!Point3Tolerance.samePoint(current.end(), next.start(), GeomTolerance.POSITION_EPS)) {
                report.addError(
                        ValidationCode.LOOP_CONTINUITY_BROKEN,
                        loop.stepId(),
                        "LoopGeom continuity broken between edges at indices " + i + " and " + (i + 1)
                );
            }
        }

        OrientedEdgeGeom first = loop.edges().getFirst();
        OrientedEdgeGeom last = loop.edges().getLast();

        if (!Point3Tolerance.samePoint(last.end(), first.start(), GeomTolerance.POSITION_EPS)) {
            report.addError(
                    ValidationCode.LOOP_NOT_CLOSED,
                    loop.stepId(),
                    "LoopGeom is not closed"
            );
        }

        return report;
    }
}