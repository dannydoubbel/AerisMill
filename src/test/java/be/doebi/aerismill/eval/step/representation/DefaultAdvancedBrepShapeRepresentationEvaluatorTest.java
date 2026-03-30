package be.doebi.aerismill.eval.step.representation;



import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.eval.step.curve.DefaultCurveEvaluator;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.eval.step.surface.DefaultSurfaceEvaluator;
import be.doebi.aerismill.eval.step.surface.SurfaceEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultEdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultFaceGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultLoopGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultManifoldSolidBrepEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultOrientedEdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultShellGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultSolidWithVoidsGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultVertexGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.EdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.FaceGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.LoopGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.ManifoldSolidBrepEvaluator;
import be.doebi.aerismill.eval.step.topology.OrientedEdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.ShellGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.SolidWithVoidsGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.VertexGeomEvaluator;
import be.doebi.aerismill.model.geom.representation.EvaluatedBrepRepresentation;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Plane;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAdvancedBrepShapeRepresentationEvaluatorTest {

    @Test
    void evaluateRepresentation_shouldBuildEvaluatedBrepRepresentation() {
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

        ClosedShell shell1 = new ClosedShell("#140", "('S1',(#130))", "S1", List.of("#130"));
        ClosedShell shell2 = new ClosedShell("#141", "('S2',(#131))", "S2", List.of("#131"));

        ManifoldSolidBrep manifoldSolid = new ManifoldSolidBrep("#150", "('MSB',#140)", "MSB", "#140");
        BrepWithVoids solidWithVoids = new BrepWithVoids("#151", "('BV',#140,(#141))", "BV", "#140", List.of("#141"));

        AdvancedBrepShapeRepresentation representation = new AdvancedBrepShapeRepresentation(
                "#160",
                "('ABSR',(#150,#151),$)",
                "ABSR",
                List.of("#150", "#151"),
                null
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
        model.addEntity(shell1);
        model.addEntity(shell2);
        model.addEntity(manifoldSolid);
        model.addEntity(solidWithVoids);
        model.addEntity(representation);
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
        ManifoldSolidBrepEvaluator manifoldSolidBrepEvaluator =
                new DefaultManifoldSolidBrepEvaluator(context, shellGeomEvaluator);
        SolidWithVoidsGeomEvaluator solidWithVoidsGeomEvaluator =
                new DefaultSolidWithVoidsGeomEvaluator(context, shellGeomEvaluator);

        DefaultAdvancedBrepShapeRepresentationEvaluator evaluator =
                new DefaultAdvancedBrepShapeRepresentationEvaluator(
                        context,
                        manifoldSolidBrepEvaluator,
                        solidWithVoidsGeomEvaluator
                );

        EvaluatedBrepRepresentation result = evaluator.evaluateRepresentation(representation);

        assertEquals("#160", result.stepId());

        assertEquals(1, result.manifoldSolids().size());
        assertEquals("#150", result.manifoldSolids().get(0).stepId());
        assertEquals("#140", result.manifoldSolids().get(0).outerShell().stepId());

        assertEquals(1, result.solidsWithVoids().size());
        assertEquals("#151", result.solidsWithVoids().get(0).stepId());
        assertEquals("#140", result.solidsWithVoids().get(0).outerShell().stepId());
        assertEquals(1, result.solidsWithVoids().get(0).voidShells().size());
        assertEquals("#141", result.solidsWithVoids().get(0).voidShells().get(0).stepId());
    }

    @Test
    void evaluateRepresentation_shouldIgnoreUnsupportedItems() {
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

        ClosedShell shell1 = new ClosedShell("#140", "('S1',(#130))", "S1", List.of("#130"));
        ManifoldSolidBrep manifoldSolid = new ManifoldSolidBrep("#150", "('MSB',#140)", "MSB", "#140");

        CartesianPoint unsupportedPoint = new CartesianPoint("#170", "('U',(9.0,9.0,9.0))", "U", List.of(9.0, 9.0, 9.0));

        AdvancedBrepShapeRepresentation representation = new AdvancedBrepShapeRepresentation(
                "#160",
                "('ABSR',(#150,#170),$)",
                "ABSR",
                List.of("#150", "#170"),
                null
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
        model.addEntity(shell1);
        model.addEntity(manifoldSolid);
        model.addEntity(unsupportedPoint);
        model.addEntity(representation);
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
        ManifoldSolidBrepEvaluator manifoldSolidBrepEvaluator =
                new DefaultManifoldSolidBrepEvaluator(context, shellGeomEvaluator);
        SolidWithVoidsGeomEvaluator solidWithVoidsGeomEvaluator =
                new DefaultSolidWithVoidsGeomEvaluator(context, shellGeomEvaluator);

        DefaultAdvancedBrepShapeRepresentationEvaluator evaluator =
                new DefaultAdvancedBrepShapeRepresentationEvaluator(
                        context,
                        manifoldSolidBrepEvaluator,
                        solidWithVoidsGeomEvaluator
                );

        EvaluatedBrepRepresentation result = evaluator.evaluateRepresentation(representation);

        assertEquals("#160", result.stepId());
        assertEquals(1, result.manifoldSolids().size());
        assertEquals("#150", result.manifoldSolids().get(0).stepId());
        assertEquals(0, result.solidsWithVoids().size());
    }
}