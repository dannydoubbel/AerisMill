package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.eval.step.curve.DefaultCurveEvaluator;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.eval.step.surface.DefaultSurfaceEvaluator;
import be.doebi.aerismill.eval.step.surface.SurfaceEvaluator;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Plane;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.OrientedClosedShell;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultSolidWithVoidsGeomEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateSolid_shouldBuildSolidWithVoidsGeom() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));

        Axis2Placement3D placement1 = new Axis2Placement3D("#30", "('A1',#10,#20,#21)", "A1", "#10", "#20", "#21");
        Axis2Placement3D placement2 = new Axis2Placement3D("#31", "('A2',#10,#20,#21)", "A2", "#10", "#20", "#21");

        Plane plane1 = new Plane("#40", "('P1',#30)", "P1", "#30");
        Plane plane2 = new Plane("#41", "('P2',#31)", "P2", "#31");

        CartesianPoint p1 = new CartesianPoint("#50", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#51", "('P2',(1.0,0.0,0.0))", "P2", List.of(1.0, 0.0, 0.0));
        CartesianPoint p3 = new CartesianPoint("#52", "('P3',(0.0,1.0,0.0))", "P3", List.of(0.0, 1.0, 0.0));

        VertexPoint v1 = new VertexPoint("#60", "('V1',#50)", "V1", "#50");
        VertexPoint v2 = new VertexPoint("#61", "('V2',#51)", "V2", "#51");
        VertexPoint v3 = new VertexPoint("#62", "('V3',#52)", "V3", "#52");

        Vector vx = new Vector("#70", "('VX',#22,1.0)", "VX", "#22", 1.0);
        Line l1 = new Line("#80", "('L1',#50,#70)", "L1", "#50", "#70");
        Line l2 = new Line("#81", "('L2',#52,#70)", "L2", "#52", "#70");

        EdgeCurve e1 = new EdgeCurve("#90", "('E1',#60,#61,#80,.T.)", "E1", "#60", "#61", "#80", true);
        EdgeCurve e2 = new EdgeCurve("#91", "('E2',#62,#61,#81,.T.)", "E2", "#62", "#61", "#81", true);

        OrientedEdge oe1 = new OrientedEdge("#100", "('OE1',*,*,#90,.T.)", "OE1", null, null, "#90", true);
        OrientedEdge oe2 = new OrientedEdge("#101", "('OE2',*,*,#91,.T.)", "OE2", null, null, "#91", true);

        EdgeLoop loop1 = new EdgeLoop("#110", "('L1',(#100))", "L1", List.of("#100"));
        EdgeLoop loop2 = new EdgeLoop("#111", "('L2',(#101))", "L2", List.of("#101"));

        FaceOuterBound bound1 = new FaceOuterBound("#120", "('B1',#110,.T.)", "B1", "#110", true);
        FaceOuterBound bound2 = new FaceOuterBound("#121", "('B2',#111,.T.)", "B2", "#111", true);

        AdvancedFace face1 = new AdvancedFace("#130", "('F1',(#120),#40,.T.)", "F1", List.of("#120"), "#40", true);
        AdvancedFace face2 = new AdvancedFace("#131", "('F2',(#121),#41,.F.)", "F2", List.of("#121"), "#41", false);

        ClosedShell outerShell = new ClosedShell("#140", "('OUTER',(#130))", "OUTER", List.of("#130"));
        ClosedShell voidShell = new ClosedShell("#141", "('VOID',(#131))", "VOID", List.of("#131"));

        BrepWithVoids brep = new BrepWithVoids(
                "#150",
                "('BREP',#140,(#141))",
                "BREP",
                "#140",
                List.of("#141")
        );

        model.addEntity(origin);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(dx);
        model.addEntity(placement1);
        model.addEntity(placement2);
        model.addEntity(plane1);
        model.addEntity(plane2);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(p3);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(v3);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(l2);
        model.addEntity(e1);
        model.addEntity(e2);
        model.addEntity(oe1);
        model.addEntity(oe2);
        model.addEntity(loop1);
        model.addEntity(loop2);
        model.addEntity(bound1);
        model.addEntity(bound2);
        model.addEntity(face1);
        model.addEntity(face2);
        model.addEntity(outerShell);
        model.addEntity(voidShell);
        model.addEntity(brep);
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
        FaceGeomEvaluator faceGeomEvaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);
        ShellGeomEvaluator shellGeomEvaluator = new DefaultShellGeomEvaluator(context, faceGeomEvaluator);
        DefaultSolidWithVoidsGeomEvaluator evaluator =
                new DefaultSolidWithVoidsGeomEvaluator(context, shellGeomEvaluator);

        SolidWithVoidsGeom result = evaluator.evaluateSolid(brep);

        assertEquals("#150", result.stepId());
        assertEquals("#140", result.outerShell().stepId());
        assertEquals(1, result.outerShell().faces().size());

        assertEquals(1, result.voidShells().size());
        assertEquals("#141", result.voidShells().get(0).stepId());
        assertEquals(1, result.voidShells().get(0).faces().size());

        assertEquals("#130", result.outerShell().faces().get(0).stepId());
        assertEquals("#131", result.voidShells().get(0).faces().get(0).stepId());

        assertEquals(0.0, result.outerShell().faces().get(0).surface().normalAt(0.0, 0.0).x(), EPS);
        assertEquals(0.0, result.outerShell().faces().get(0).surface().normalAt(0.0, 0.0).y(), EPS);
        assertEquals(1.0, result.outerShell().faces().get(0).surface().normalAt(0.0, 0.0).z(), EPS);
    }

    @Test
    void evaluateSolid_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));

        Axis2Placement3D placement = new Axis2Placement3D("#30", "('A',#10,#20,#21)", "A", "#10", "#20", "#21");
        Plane plane = new Plane("#40", "('P',#30)", "P", "#30");

        CartesianPoint p1 = new CartesianPoint("#50", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#51", "('P2',(1.0,0.0,0.0))", "P2", List.of(1.0, 0.0, 0.0));

        VertexPoint v1 = new VertexPoint("#60", "('V1',#50)", "V1", "#50");
        VertexPoint v2 = new VertexPoint("#61", "('V2',#51)", "V2", "#51");

        Vector vx = new Vector("#70", "('VX',#22,1.0)", "VX", "#22", 1.0);
        Line l1 = new Line("#80", "('L1',#50,#70)", "L1", "#50", "#70");

        EdgeCurve e1 = new EdgeCurve("#90", "('E1',#60,#61,#80,.T.)", "E1", "#60", "#61", "#80", true);
        OrientedEdge oe1 = new OrientedEdge("#100", "('OE1',*,*,#90,.T.)", "OE1", null, null, "#90", true);
        EdgeLoop loop1 = new EdgeLoop("#110", "('L1',(#100))", "L1", List.of("#100"));
        FaceOuterBound bound1 = new FaceOuterBound("#120", "('B1',#110,.T.)", "B1", "#110", true);
        AdvancedFace face1 = new AdvancedFace("#130", "('F1',(#120),#40,.T.)", "F1", List.of("#120"), "#40", true);

        ClosedShell outerShell = new ClosedShell("#140", "('OUTER',(#130))", "OUTER", List.of("#130"));
        BrepWithVoids brep = new BrepWithVoids(
                "#150",
                "('BREP',#140,())",
                "BREP",
                "#140",
                List.of()
        );

        model.addEntity(origin);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(dx);
        model.addEntity(placement);
        model.addEntity(plane);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(e1);
        model.addEntity(oe1);
        model.addEntity(loop1);
        model.addEntity(bound1);
        model.addEntity(face1);
        model.addEntity(outerShell);
        model.addEntity(brep);
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
        FaceGeomEvaluator faceGeomEvaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);
        ShellGeomEvaluator shellGeomEvaluator = new DefaultShellGeomEvaluator(context, faceGeomEvaluator);
        DefaultSolidWithVoidsGeomEvaluator evaluator =
                new DefaultSolidWithVoidsGeomEvaluator(context, shellGeomEvaluator);

        SolidWithVoidsGeom first = evaluator.evaluateSolid(brep);
        SolidWithVoidsGeom second = evaluator.evaluateSolid(brep);

        assertSame(first, second);
    }

    @Test
    void evaluateSolid_shouldRecoverClosedShellsFromRefs_whenBrepIsUnresolved() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        Direction z = new Direction("#20", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction x = new Direction("#21", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Direction dx = new Direction("#22", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));

        Axis2Placement3D placement1 = new Axis2Placement3D("#30", "('A1',#10,#20,#21)", "A1", "#10", "#20", "#21");
        Axis2Placement3D placement2 = new Axis2Placement3D("#31", "('A2',#10,#20,#21)", "A2", "#10", "#20", "#21");

        Plane plane1 = new Plane("#40", "('P1',#30)", "P1", "#30");
        Plane plane2 = new Plane("#41", "('P2',#31)", "P2", "#31");

        CartesianPoint p1 = new CartesianPoint("#50", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#51", "('P2',(1.0,0.0,0.0))", "P2", List.of(1.0, 0.0, 0.0));
        CartesianPoint p3 = new CartesianPoint("#52", "('P3',(0.0,1.0,0.0))", "P3", List.of(0.0, 1.0, 0.0));

        VertexPoint v1 = new VertexPoint("#60", "('V1',#50)", "V1", "#50");
        VertexPoint v2 = new VertexPoint("#61", "('V2',#51)", "V2", "#51");
        VertexPoint v3 = new VertexPoint("#62", "('V3',#52)", "V3", "#52");

        Vector vx = new Vector("#70", "('VX',#22,1.0)", "VX", "#22", 1.0);
        Line l1 = new Line("#80", "('L1',#50,#70)", "L1", "#50", "#70");
        Line l2 = new Line("#81", "('L2',#52,#70)", "L2", "#52", "#70");

        EdgeCurve e1 = new EdgeCurve("#90", "('E1',#60,#61,#80,.T.)", "E1", "#60", "#61", "#80", true);
        EdgeCurve e2 = new EdgeCurve("#91", "('E2',#62,#61,#81,.T.)", "E2", "#62", "#61", "#81", true);

        OrientedEdge oe1 = new OrientedEdge("#100", "('OE1',*,*,#90,.T.)", "OE1", null, null, "#90", true);
        OrientedEdge oe2 = new OrientedEdge("#101", "('OE2',*,*,#91,.T.)", "OE2", null, null, "#91", true);

        EdgeLoop loop1 = new EdgeLoop("#110", "('L1',(#100))", "L1", List.of("#100"));
        EdgeLoop loop2 = new EdgeLoop("#111", "('L2',(#101))", "L2", List.of("#101"));

        FaceOuterBound bound1 = new FaceOuterBound("#120", "('B1',#110,.T.)", "B1", "#110", true);
        FaceOuterBound bound2 = new FaceOuterBound("#121", "('B2',#111,.T.)", "B2", "#111", true);

        AdvancedFace face1 = new AdvancedFace("#130", "('F1',(#120),#40,.T.)", "F1", List.of("#120"), "#40", true);
        AdvancedFace face2 = new AdvancedFace("#131", "('F2',(#121),#41,.F.)", "F2", List.of("#121"), "#41", false);

        ClosedShell outerShell = new ClosedShell("#140", "('OUTER',(#130))", "OUTER", List.of("#130"));
        ClosedShell voidShell = new ClosedShell("#141", "('VOID',(#131))", "VOID", List.of("#131"));
        OrientedClosedShell orientedOuter = new OrientedClosedShell("#142", "('OOUT',#140,.T.)", "OOUT", "#140", true);
        OrientedClosedShell orientedVoid = new OrientedClosedShell("#143", "('OVOID',#141,.T.)", "OVOID", "#141", true);

        BrepWithVoids brep = new BrepWithVoids(
                "#150",
                "('BREP',#142,(#143))",
                "BREP",
                "#142",
                List.of("#143")
        );

        model.addEntity(origin);
        model.addEntity(z);
        model.addEntity(x);
        model.addEntity(dx);
        model.addEntity(placement1);
        model.addEntity(placement2);
        model.addEntity(plane1);
        model.addEntity(plane2);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(p3);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(v3);
        model.addEntity(vx);
        model.addEntity(l1);
        model.addEntity(l2);
        model.addEntity(e1);
        model.addEntity(e2);
        model.addEntity(oe1);
        model.addEntity(oe2);
        model.addEntity(loop1);
        model.addEntity(loop2);
        model.addEntity(bound1);
        model.addEntity(bound2);
        model.addEntity(face1);
        model.addEntity(face2);
        model.addEntity(outerShell);
        model.addEntity(voidShell);
        model.addEntity(orientedOuter);
        model.addEntity(orientedVoid);
        model.addEntity(brep);

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(context, orientedEdgeGeomEvaluator);
        FaceGeomEvaluator faceGeomEvaluator = new DefaultFaceGeomEvaluator(context, surfaceEvaluator, loopGeomEvaluator);
        ShellGeomEvaluator shellGeomEvaluator = new DefaultShellGeomEvaluator(context, faceGeomEvaluator);
        DefaultSolidWithVoidsGeomEvaluator evaluator =
                new DefaultSolidWithVoidsGeomEvaluator(context, shellGeomEvaluator);

        SolidWithVoidsGeom result = evaluator.evaluateSolid(brep);

        assertEquals("#140", result.outerShell().stepId());
        assertEquals(1, result.voidShells().size());
        assertEquals("#141", result.voidShells().get(0).stepId());
    }
}
