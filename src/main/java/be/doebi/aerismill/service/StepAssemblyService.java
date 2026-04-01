package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.DefaultStepToGeomAssembler;
import be.doebi.aerismill.assemble.step.geom.StepToGeomAssembler;
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
import be.doebi.aerismill.eval.step.topology.DefaultOrientedEdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultShellGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultSolidGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.DefaultVertexGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.EdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.FaceGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.LoopGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.OrientedEdgeGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.ShellGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.SolidGeomEvaluator;
import be.doebi.aerismill.eval.step.topology.VertexGeomEvaluator;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.validate.geom.topology.DefaultFaceGeomValidator;
import be.doebi.aerismill.validate.geom.topology.DefaultLoopGeomValidator;
import be.doebi.aerismill.validate.geom.topology.DefaultShellGeomValidator;
import be.doebi.aerismill.validate.geom.topology.DefaultSolidGeomValidator;
import be.doebi.aerismill.validate.geom.topology.DefaultTopologyValidationService;
import be.doebi.aerismill.validate.geom.topology.TopologyValidationService;

public class StepAssemblyService {

    public AssemblyResult assemble(StepModel stepModel) {
        StepToGeomAssembler assembler = createAssembler(stepModel);
        return assembler.assemble(stepModel);
    }

    private StepToGeomAssembler createAssembler(StepModel stepModel) {
        StepEvaluationContext context = new StepEvaluationContext(stepModel);

        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(
                context,
                curveEvaluator,
                vertexGeomEvaluator
        );
        OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator = new DefaultOrientedEdgeGeomEvaluator(
                context,
                edgeGeomEvaluator
        );
        LoopGeomEvaluator loopGeomEvaluator = new DefaultLoopGeomEvaluator(
                context,
                orientedEdgeGeomEvaluator
        );
        FaceGeomEvaluator faceGeomEvaluator = new DefaultFaceGeomEvaluator(
                context,
                surfaceEvaluator,
                loopGeomEvaluator
        );
        ShellGeomEvaluator shellGeomEvaluator = new DefaultShellGeomEvaluator(
                context,
                faceGeomEvaluator
        );
        SolidGeomEvaluator solidGeomEvaluator = new DefaultSolidGeomEvaluator(
                context,
                shellGeomEvaluator
        );

        TopologyValidationService topologyValidationService = new DefaultTopologyValidationService(
                new DefaultLoopGeomValidator(),
                new DefaultFaceGeomValidator(),
                new DefaultShellGeomValidator(),
                new DefaultSolidGeomValidator()
        );

        return new DefaultStepToGeomAssembler(
                solidGeomEvaluator,
                topologyValidationService
        );
    }
}