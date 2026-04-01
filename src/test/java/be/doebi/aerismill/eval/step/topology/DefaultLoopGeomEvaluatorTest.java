package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.eval.step.curve.DefaultCurveEvaluator;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultLoopGeomEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateLoop_shouldBuildLoopGeom() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));
        CartesianPoint p3 = new CartesianPoint("#12", "('P3',(5.0,5.0,0.0))", "P3", List.of(5.0, 5.0, 0.0));

        VertexPoint v1 = new VertexPoint("#20", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#21", "('V2',#11)", "V2", "#11");
        VertexPoint v3 = new VertexPoint("#22", "('V3',#12)", "V3", "#12");

        Direction dx = new Direction("#30", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Direction dy = new Direction("#31", "('DY',(0.0,1.0,0.0))", "DY", List.of(0.0, 1.0, 0.0));

        Vector vx = new Vector("#40", "('VX',#30,5.0)", "VX", "#30", 5.0);
        Vector vy = new Vector("#41", "('VY',#31,5.0)", "VY", "#31", 5.0);

        Line l1 = new Line("#50", "('L1',#10,#40)", "L1", "#10", "#40");
        Line l2 = new Line("#51", "('L2',#11,#41)", "L2", "#11", "#41");

        EdgeCurve e1 = new EdgeCurve("#60", "('E1',#20,#21,#50,.T.)", "E1", "#20", "#21", "#50", true);
        EdgeCurve e2 = new EdgeCurve("#61", "('E2',#21,#22,#51,.T.)", "E2", "#21", "#22", "#51", true);

        OrientedEdge oe1 = new OrientedEdge("#70", "('OE1',*,*,#60,.T.)", "OE1", null, null, "#60", true);
        OrientedEdge oe2 = new OrientedEdge("#71", "('OE2',*,*,#61,.T.)", "OE2", null, null, "#61", true);

        EdgeLoop loop = new EdgeLoop("#80", "('LOOP',(#70,#71))", "LOOP", List.of("#70", "#71"));

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(p3);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(v3);
        model.addEntity(dx);
        model.addEntity(dy);
        model.addEntity(vx);
        model.addEntity(vy);
        model.addEntity(l1);
        model.addEntity(l2);
        model.addEntity(e1);
        model.addEntity(e2);
        model.addEntity(oe1);
        model.addEntity(oe2);
        model.addEntity(loop);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        DefaultLoopGeomEvaluator evaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);

        LoopGeom result = evaluator.evaluateLoop(loop);

        assertEquals("#80", result.stepId());
        assertEquals(2, result.edges().size());

        assertEquals(0.0, result.edges().get(0).start().x(), EPS);
        assertEquals(0.0, result.edges().get(0).start().y(), EPS);
        assertEquals(0.0, result.edges().get(0).start().z(), EPS);

        assertEquals(5.0, result.edges().get(0).end().x(), EPS);
        assertEquals(0.0, result.edges().get(0).end().y(), EPS);
        assertEquals(0.0, result.edges().get(0).end().z(), EPS);

        assertEquals(5.0, result.edges().get(1).start().x(), EPS);
        assertEquals(0.0, result.edges().get(1).start().y(), EPS);
        assertEquals(0.0, result.edges().get(1).start().z(), EPS);

        assertEquals(5.0, result.edges().get(1).end().x(), EPS);
        assertEquals(5.0, result.edges().get(1).end().y(), EPS);
        assertEquals(0.0, result.edges().get(1).end().z(), EPS);
    }

    @Test
    void evaluateLoop_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(1.0,0.0,0.0))", "P2", List.of(1.0, 0.0, 0.0));

        VertexPoint v1 = new VertexPoint("#20", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#21", "('V2',#11)", "V2", "#11");

        Direction dx = new Direction("#30", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Vector vx = new Vector("#40", "('VX',#30,1.0)", "VX", "#30", 1.0);
        Line l1 = new Line("#50", "('L1',#10,#40)", "L1", "#10", "#40");

        EdgeCurve e1 = new EdgeCurve("#60", "('E1',#20,#21,#50,.T.)", "E1", "#20", "#21", "#50", true);
        OrientedEdge oe1 = new OrientedEdge("#70", "('OE1',*,*,#60,.T.)", "OE1", null, null, "#60", true);
        EdgeLoop loop = new EdgeLoop("#80", "('LOOP',(#70))", "LOOP", List.of("#70"));

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(dx);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(e1);
        model.addEntity(oe1);
        model.addEntity(loop);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        DefaultLoopGeomEvaluator evaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);

        LoopGeom first = evaluator.evaluateLoop(loop);
        LoopGeom second = evaluator.evaluateLoop(loop);

        assertSame(first, second);
    }

    @Test
    void evaluateLoop_shouldPreserveEdgeOrderAndContinuity() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));
        CartesianPoint p3 = new CartesianPoint("#12", "('P3',(5.0,5.0,0.0))", "P3", List.of(5.0, 5.0, 0.0));

        Direction dx = new Direction("#20", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Direction dy = new Direction("#21", "('DY',(0.0,1.0,0.0))", "DY", List.of(0.0, 1.0, 0.0));

        Vector vx = new Vector("#22", "('VX',#20,5.0)", "VX", "#20", 5.0);
        Vector vy = new Vector("#23", "('VY',#21,5.0)", "VY", "#21", 5.0);

        Line l1 = new Line("#24", "('L1',#10,#22)", "L1", "#10", "#22");
        Line l2 = new Line("#25", "('L2',#11,#23)", "L2", "#11", "#23");

        VertexPoint v1 = new VertexPoint("#30", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#31", "('V2',#11)", "V2", "#11");
        VertexPoint v3 = new VertexPoint("#32", "('V3',#12)", "V3", "#12");

        EdgeCurve e1 = new EdgeCurve("#40", "('E1',#30,#31,#24,.T.)", "E1", "#30", "#31", "#24", true);
        EdgeCurve e2 = new EdgeCurve("#41", "('E2',#31,#32,#25,.T.)", "E2", "#31", "#32", "#25", true);

        OrientedEdge oe1 = new OrientedEdge("#50", "('OE1',*,*,#40,.T.)", "OE1", null, null, "#40", true);
        OrientedEdge oe2 = new OrientedEdge("#51", "('OE2',*,*,#41,.T.)", "OE2", null, null, "#41", true);

        EdgeLoop loop = new EdgeLoop("#60", "('LOOP',(#50,#51))", "LOOP", List.of("#50", "#51"));

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(p3);
        model.addEntity(dx);
        model.addEntity(dy);
        model.addEntity(vx);
        model.addEntity(vy);
        model.addEntity(l1);
        model.addEntity(l2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(v3);
        model.addEntity(e1);
        model.addEntity(e2);
        model.addEntity(oe1);
        model.addEntity(oe2);
        model.addEntity(loop);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        DefaultLoopGeomEvaluator evaluator =
                new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);

        LoopGeom result = evaluator.evaluateLoop(loop);

        assertEquals("#60", result.stepId());
        assertEquals(2, result.edges().size());

        assertEquals(0.0, result.edges().get(0).start().x(), EPS);
        assertEquals(5.0, result.edges().get(0).end().x(), EPS);

        assertEquals(
                result.edges().get(0).end().x(),
                result.edges().get(1).start().x(),
                EPS
        );
        assertEquals(
                result.edges().get(0).end().y(),
                result.edges().get(1).start().y(),
                EPS
        );
        assertEquals(
                result.edges().get(0).end().z(),
                result.edges().get(1).start().z(),
                EPS
        );
    }

    @Test
    void evaluateLoop_shouldPreserveClosureForClosedTriangle() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));
        CartesianPoint p3 = new CartesianPoint("#12", "('P3',(5.0,5.0,0.0))", "P3", List.of(5.0, 5.0, 0.0));

        Direction dx = new Direction("#20", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Direction dy = new Direction("#21", "('DY',(0.0,1.0,0.0))", "DY", List.of(0.0, 1.0, 0.0));
        Direction dback = new Direction("#22", "('DB',(-1.0,-1.0,0.0))", "DB", List.of(-1.0, -1.0, 0.0));

        Vector vx = new Vector("#23", "('VX',#20,5.0)", "VX", "#20", 5.0);
        Vector vy = new Vector("#24", "('VY',#21,5.0)", "VY", "#21", 5.0);
        Vector vb = new Vector("#25", "('VB',#22,7.0710678118654755)", "VB", "#22", 7.0710678118654755);

        Line l1 = new Line("#26", "('L1',#10,#23)", "L1", "#10", "#23");
        Line l2 = new Line("#27", "('L2',#11,#24)", "L2", "#11", "#24");
        Line l3 = new Line("#28", "('L3',#12,#25)", "L3", "#12", "#25");

        VertexPoint v1 = new VertexPoint("#30", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#31", "('V2',#11)", "V2", "#11");
        VertexPoint v3 = new VertexPoint("#32", "('V3',#12)", "V3", "#12");

        EdgeCurve e1 = new EdgeCurve("#40", "('E1',#30,#31,#26,.T.)", "E1", "#30", "#31", "#26", true);
        EdgeCurve e2 = new EdgeCurve("#41", "('E2',#31,#32,#27,.T.)", "E2", "#31", "#32", "#27", true);
        EdgeCurve e3 = new EdgeCurve("#42", "('E3',#32,#30,#28,.T.)", "E3", "#32", "#30", "#28", true);

        OrientedEdge oe1 = new OrientedEdge("#50", "('OE1',*,*,#40,.T.)", "OE1", null, null, "#40", true);
        OrientedEdge oe2 = new OrientedEdge("#51", "('OE2',*,*,#41,.T.)", "OE2", null, null, "#41", true);
        OrientedEdge oe3 = new OrientedEdge("#52", "('OE3',*,*,#42,.T.)", "OE3", null, null, "#42", true);

        EdgeLoop loop = new EdgeLoop("#60", "('LOOP',(#50,#51,#52))", "LOOP", List.of("#50", "#51", "#52"));

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(p3);
        model.addEntity(dx);
        model.addEntity(dy);
        model.addEntity(dback);
        model.addEntity(vx);
        model.addEntity(vy);
        model.addEntity(vb);
        model.addEntity(l1);
        model.addEntity(l2);
        model.addEntity(l3);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(v3);
        model.addEntity(e1);
        model.addEntity(e2);
        model.addEntity(e3);
        model.addEntity(oe1);
        model.addEntity(oe2);
        model.addEntity(oe3);
        model.addEntity(loop);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        DefaultLoopGeomEvaluator evaluator =
                new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);

        LoopGeom result = evaluator.evaluateLoop(loop);

        assertEquals("#60", result.stepId());
        assertEquals(3, result.edges().size());

        assertEquals(
                result.edges().get(2).end().x(),
                result.edges().get(0).start().x(),
                EPS
        );
        assertEquals(
                result.edges().get(2).end().y(),
                result.edges().get(0).start().y(),
                EPS
        );
        assertEquals(
                result.edges().get(2).end().z(),
                result.edges().get(0).start().z(),
                EPS
        );
    }



}