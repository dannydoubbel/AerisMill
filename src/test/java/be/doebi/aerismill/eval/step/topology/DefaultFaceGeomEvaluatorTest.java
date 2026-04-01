package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.eval.step.curve.DefaultCurveEvaluator;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.eval.step.surface.DefaultSurfaceEvaluator;
import be.doebi.aerismill.eval.step.surface.SurfaceEvaluator;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.*;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceBound;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultFaceGeomEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateFace_shouldBuildFaceGeom_forPlaneWithOuterBound() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        CartesianPoint p1 = new CartesianPoint("#11", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#12", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));

        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));

        Axis2Placement3D placement = new Axis2Placement3D("#30", "('A',#10,#20,#21)", "A", "#10", "#20", "#21");
        Plane plane = new Plane("#31", "('PL',#30)", "PL", "#30");

        VertexPoint v1 = new VertexPoint("#40", "('V1',#11)", "V1", "#11");
        VertexPoint v2 = new VertexPoint("#41", "('V2',#12)", "V2", "#12");

        Vector vx = new Vector("#50", "('VX',#22,5.0)", "VX", "#22", 5.0);
        Line l1 = new Line("#60", "('L1',#11,#50)", "L1", "#11", "#50");
        EdgeCurve e1 = new EdgeCurve("#70", "('E1',#40,#41,#60,.T.)", "E1", "#40", "#41", "#60", true);
        OrientedEdge oe1 = new OrientedEdge("#80", "('OE1',*,*,#70,.T.)", "OE1", null, null, "#70", true);
        EdgeLoop loop = new EdgeLoop("#90", "('LOOP',(#80))", "LOOP", List.of("#80"));

        FaceOuterBound outerBound = new FaceOuterBound("#100", "('FOB',#90,.T.)", "FOB", "#90", true);

        AdvancedFace face = new AdvancedFace(
                "#110",
                "('FACE',(#100),#31,.T.)",
                "FACE",
                List.of("#100"),
                "#31",
                true
        );

        model.addEntity(origin);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(dx);
        model.addEntity(placement);
        model.addEntity(plane);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(e1);
        model.addEntity(oe1);
        model.addEntity(loop);
        model.addEntity(outerBound);
        model.addEntity(face);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);
        DefaultFaceGeomEvaluator evaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);

        FaceGeom result = evaluator.evaluateFace(face);

        assertEquals("#110", result.stepId());
        assertEquals(true, result.sameSense());
        assertEquals(1, result.bounds().size());

        assertEquals(0.0, result.bounds().get(0).edges().get(0).start().x(), EPS);
        assertEquals(0.0, result.bounds().get(0).edges().get(0).start().y(), EPS);
        assertEquals(0.0, result.bounds().get(0).edges().get(0).start().z(), EPS);

        assertEquals(5.0, result.bounds().get(0).edges().get(0).end().x(), EPS);
        assertEquals(0.0, result.bounds().get(0).edges().get(0).end().y(), EPS);
        assertEquals(0.0, result.bounds().get(0).edges().get(0).end().z(), EPS);

        assertEquals(0.0, result.surface().normalAt(0.0, 0.0).x(), EPS);
        assertEquals(0.0, result.surface().normalAt(0.0, 0.0).y(), EPS);
        assertEquals(1.0, result.surface().normalAt(0.0, 0.0).z(), EPS);
    }

    @Test
    void evaluateFace_shouldBuildFaceGeom_forCylinderWithOuterAndInnerBound() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        CartesianPoint p1 = new CartesianPoint("#11", "('P1',(5.0,0.0,0.0))", "P1", List.of(5.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#12", "('P2',(0.0,5.0,0.0))", "P2", List.of(0.0, 5.0, 0.0));
        CartesianPoint p3 = new CartesianPoint("#13", "('P3',(0.0,0.0,0.0))", "P3", List.of(0.0, 0.0, 0.0));
        CartesianPoint p4 = new CartesianPoint("#14", "('P4',(0.0,0.0,2.0))", "P4", List.of(0.0, 0.0, 2.0));

        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Direction dz = new Direction("#22", "('DZ',(0.0,0.0,1.0))", "DZ", List.of(0.0, 0.0, 1.0));

        Axis2Placement3D placement = new Axis2Placement3D("#30", "('A',#10,#20,#21)", "A", "#10", "#20", "#21");
        CylindricalSurface cylindricalSurface = new CylindricalSurface("#31", "('CS',#30,5.0)", "CS", "#30", 5.0);

        VertexPoint v1 = new VertexPoint("#40", "('V1',#11)", "V1", "#11");
        VertexPoint v2 = new VertexPoint("#41", "('V2',#12)", "V2", "#12");
        VertexPoint v3 = new VertexPoint("#42", "('V3',#13)", "V3", "#13");
        VertexPoint v4 = new VertexPoint("#43", "('V4',#14)", "V4", "#14");

        Vector vz = new Vector("#50", "('VZ',#22,2.0)", "VZ", "#22", 2.0);
        Line l1 = new Line("#60", "('L1',#13,#50)", "L1", "#13", "#50");

        Axis2Placement3D circlePlacement = new Axis2Placement3D("#61", "('CA',#10,#20,#21)", "CA", "#10", "#20", "#21");
        be.doebi.aerismill.model.step.geometry.Circle circle =
                new be.doebi.aerismill.model.step.geometry.Circle("#62", "('C',#61,5.0)", "C", "#61", 5.0);

        EdgeCurve e1 = new EdgeCurve("#70", "('E1',#40,#41,#62,.T.)", "E1", "#40", "#41", "#62", true);
        EdgeCurve e2 = new EdgeCurve("#71", "('E2',#42,#43,#60,.T.)", "E2", "#42", "#43", "#60", true);

        OrientedEdge oe1 = new OrientedEdge("#80", "('OE1',*,*,#70,.T.)", "OE1", null, null, "#70", true);
        OrientedEdge oe2 = new OrientedEdge("#81", "('OE2',*,*,#71,.T.)", "OE2", null, null, "#71", true);

        EdgeLoop outerLoop = new EdgeLoop("#90", "('OUTER',(#80))", "OUTER", List.of("#80"));
        EdgeLoop innerLoop = new EdgeLoop("#91", "('INNER',(#81))", "INNER", List.of("#81"));

        FaceOuterBound outerBound = new FaceOuterBound("#100", "('FOB',#90,.T.)", "FOB", "#90", true);
        FaceBound innerBound = new FaceBound("#101", "('FB',#91,.T.)", "FB", "#91", true);

        AdvancedFace face = new AdvancedFace(
                "#110",
                "('FACE',(#100,#101),#31,.F.)",
                "FACE",
                List.of("#100", "#101"),
                "#31",
                false
        );

        model.addEntity(origin);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(p3);
        model.addEntity(p4);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(dz);
        model.addEntity(placement);
        model.addEntity(cylindricalSurface);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(v3);
        model.addEntity(v4);
        model.addEntity(vz);
        model.addEntity(l1);
        model.addEntity(circlePlacement);
        model.addEntity(circle);
        model.addEntity(e1);
        model.addEntity(e2);
        model.addEntity(oe1);
        model.addEntity(oe2);
        model.addEntity(outerLoop);
        model.addEntity(innerLoop);
        model.addEntity(outerBound);
        model.addEntity(innerBound);
        model.addEntity(face);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);
        DefaultFaceGeomEvaluator evaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);

        FaceGeom result = evaluator.evaluateFace(face);

        assertEquals("#110", result.stepId());
        assertEquals(false, result.sameSense());
        assertEquals(2, result.bounds().size());
        assertEquals(1, result.bounds().get(0).edges().size());
        assertEquals(1, result.bounds().get(1).edges().size());

        assertEquals(1.0, result.surface().normalAt(0.0, 0.0).x(), EPS);
        assertEquals(0.0, result.surface().normalAt(0.0, 0.0).y(), EPS);
        assertEquals(0.0, result.surface().normalAt(0.0, 0.0).z(), EPS);
    }

    @Test
    void evaluateFace_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Axis2Placement3D placement = new Axis2Placement3D("#30", "('A',#10,#20,#21)", "A", "#10", "#20", "#21");
        Plane plane = new Plane("#31", "('PL',#30)", "PL", "#30");

        CartesianPoint p1 = new CartesianPoint("#11", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#12", "('P2',(1.0,0.0,0.0))", "P2", List.of(1.0, 0.0, 0.0));
        VertexPoint v1 = new VertexPoint("#40", "('V1',#11)", "V1", "#11");
        VertexPoint v2 = new VertexPoint("#41", "('V2',#12)", "V2", "#12");

        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Vector vx = new Vector("#50", "('VX',#22,1.0)", "VX", "#22", 1.0);
        Line l1 = new Line("#60", "('L1',#11,#50)", "L1", "#11", "#50");
        EdgeCurve e1 = new EdgeCurve("#70", "('E1',#40,#41,#60,.T.)", "E1", "#40", "#41", "#60", true);
        OrientedEdge oe1 = new OrientedEdge("#80", "('OE1',*,*,#70,.T.)", "OE1", null, null, "#70", true);
        EdgeLoop loop = new EdgeLoop("#90", "('LOOP',(#80))", "LOOP", List.of("#80"));
        FaceOuterBound outerBound = new FaceOuterBound("#100", "('FOB',#90,.T.)", "FOB", "#90", true);

        AdvancedFace face = new AdvancedFace(
                "#110",
                "('FACE',(#100),#31,.T.)",
                "FACE",
                List.of("#100"),
                "#31",
                true
        );

        model.addEntity(origin);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(placement);
        model.addEntity(plane);
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
        model.addEntity(outerBound);
        model.addEntity(face);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);
        DefaultFaceGeomEvaluator evaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);

        FaceGeom first = evaluator.evaluateFace(face);
        FaceGeom second = evaluator.evaluateFace(face);

        assertSame(first, second);
    }

    @Test
    void evaluateFace_shouldBuildFaceGeom_forConicalSurfaceWithOuterBound() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        CartesianPoint p1 = new CartesianPoint("#11", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#12", "('P2',(3.0,0.0,0.0))", "P2", List.of(3.0, 0.0, 0.0));

        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));

        Axis2Placement3D placement = new Axis2Placement3D("#30", "('A',#10,#20,#21)", "A", "#10", "#20", "#21");
        be.doebi.aerismill.model.step.geometry.ConicalSurface conicalSurface =
                new be.doebi.aerismill.model.step.geometry.ConicalSurface(
                        "#31",
                        "('CS',#30,2.0,0.5)",
                        "CS",
                        "#30",
                        2.0,
                        0.5
                );

        VertexPoint v1 = new VertexPoint("#40", "('V1',#11)", "V1", "#11");
        VertexPoint v2 = new VertexPoint("#41", "('V2',#12)", "V2", "#12");

        Vector vx = new Vector("#50", "('VX',#22,3.0)", "VX", "#22", 3.0);
        Line l1 = new Line("#60", "('L1',#11,#50)", "L1", "#11", "#50");
        EdgeCurve e1 = new EdgeCurve("#70", "('E1',#40,#41,#60,.T.)", "E1", "#40", "#41", "#60", true);
        OrientedEdge oe1 = new OrientedEdge("#80", "('OE1',*,*,#70,.T.)", "OE1", null, null, "#70", true);
        EdgeLoop loop = new EdgeLoop("#90", "('LOOP',(#80))", "LOOP", List.of("#80"));

        FaceOuterBound outerBound = new FaceOuterBound("#100", "('FOB',#90,.T.)", "FOB", "#90", true);

        AdvancedFace face = new AdvancedFace(
                "#110",
                "('FACE',(#100),#31,.T.)",
                "FACE",
                List.of("#100"),
                "#31",
                true
        );

        model.addEntity(origin);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(dx);
        model.addEntity(placement);
        model.addEntity(conicalSurface);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(e1);
        model.addEntity(oe1);
        model.addEntity(loop);
        model.addEntity(outerBound);
        model.addEntity(face);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);
        DefaultFaceGeomEvaluator evaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);

        FaceGeom result = evaluator.evaluateFace(face);

        assertEquals("#110", result.stepId());
        assertEquals(true, result.sameSense());
        assertEquals(1, result.bounds().size());
        assertEquals(1, result.bounds().get(0).edges().size());

        assertEquals(0.0, result.bounds().get(0).edges().get(0).start().x(), EPS);
        assertEquals(3.0, result.bounds().get(0).edges().get(0).end().x(), EPS);


    }

    @Test
    void evaluateFace_shouldBuildFaceGeom_forBSplineSurfaceWithOuterBound() {
        StepModel model = new StepModel();

        CartesianPoint p00 = new CartesianPoint("#10", "('P00',(0.0,0.0,0.0))", "P00", List.of(0.0, 0.0, 0.0));
        CartesianPoint p01 = new CartesianPoint("#11", "('P01',(0.0,1.0,0.0))", "P01", List.of(0.0, 1.0, 0.0));
        CartesianPoint p10 = new CartesianPoint("#12", "('P10',(1.0,0.0,0.0))", "P10", List.of(1.0, 0.0, 0.0));
        CartesianPoint p11 = new CartesianPoint("#13", "('P11',(1.0,1.0,0.0))", "P11", List.of(1.0, 1.0, 0.0));

        CartesianPoint e1p1 = new CartesianPoint("#14", "('E1P1',(0.0,0.0,0.0))", "E1P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint e1p2 = new CartesianPoint("#15", "('E1P2',(1.0,0.0,0.0))", "E1P2", List.of(1.0, 0.0, 0.0));

        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));

        BSplineSurfaceWithKnots splineSurface = new BSplineSurfaceWithKnots(
                "#30",
                "('BS',1,1,((#10,#11),(#12,#13)),.UNSPECIFIED.,.F.,.F.,.F.,(2,2),(2,2),(0.0,1.0),(0.0,1.0),.UNSPECIFIED.)",
                "BS",
                1,
                1,
                List.of(
                        List.of("#10", "#11"),
                        List.of("#12", "#13")
                ),
                "UNSPECIFIED",
                be.doebi.aerismill.model.step.base.StepLogical.FALSE,
                be.doebi.aerismill.model.step.base.StepLogical.FALSE,
                be.doebi.aerismill.model.step.base.StepLogical.FALSE,
                List.of(2, 2),
                List.of(2, 2),
                List.of(0.0, 1.0),
                List.of(0.0, 1.0),
                "UNSPECIFIED"
        );

        VertexPoint v1 = new VertexPoint("#40", "('V1',#14)", "V1", "#14");
        VertexPoint v2 = new VertexPoint("#41", "('V2',#15)", "V2", "#15");

        Vector vx = new Vector("#50", "('VX',#22,1.0)", "VX", "#22", 1.0);
        Line l1 = new Line("#60", "('L1',#14,#50)", "L1", "#14", "#50");
        EdgeCurve e1 = new EdgeCurve("#70", "('E1',#40,#41,#60,.T.)", "E1", "#40", "#41", "#60", true);
        OrientedEdge oe1 = new OrientedEdge("#80", "('OE1',*,*,#70,.T.)", "OE1", null, null, "#70", true);
        EdgeLoop loop = new EdgeLoop("#90", "('LOOP',(#80))", "LOOP", List.of("#80"));

        FaceOuterBound outerBound = new FaceOuterBound("#100", "('FOB',#90,.T.)", "FOB", "#90", true);

        AdvancedFace face = new AdvancedFace(
                "#110",
                "('FACE',(#100),#30,.T.)",
                "FACE",
                List.of("#100"),
                "#30",
                true
        );

        model.addEntity(p00);
        model.addEntity(p01);
        model.addEntity(p10);
        model.addEntity(p11);
        model.addEntity(e1p1);
        model.addEntity(e1p2);
        model.addEntity(dx);
        model.addEntity(splineSurface);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(e1);
        model.addEntity(oe1);
        model.addEntity(loop);
        model.addEntity(outerBound);
        model.addEntity(face);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);
        DefaultFaceGeomEvaluator evaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);

        FaceGeom result = evaluator.evaluateFace(face);

        assertEquals("#110", result.stepId());
        assertEquals(true, result.sameSense());
        assertEquals(1, result.bounds().size());
        assertEquals(1, result.bounds().get(0).edges().size());

        assertEquals(0.0, result.bounds().get(0).edges().get(0).start().x(), EPS);
        assertEquals(1.0, result.bounds().get(0).edges().get(0).end().x(), EPS);

        assertEquals(0.0, result.surface().pointAt(0.0, 0.0).x(), EPS);
        assertEquals(0.0, result.surface().pointAt(0.0, 0.0).y(), EPS);
        assertEquals(0.0, result.surface().pointAt(0.0, 0.0).z(), EPS);
    }

}