package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.ConicalSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.*;
import be.doebi.aerismill.tessellation.curve.DefaultEdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.RecordingPolygonTriangulator;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultFaceTessellatorTest {
    @Test
    void tessellate_dispatchesConicalFace() {
        RecordingPolygonTriangulator triangulator = new RecordingPolygonTriangulator();
        triangulator.stubResult(List.of(
                new int[]{0, 1, 2},
                new int[]{0, 2, 3}
        ));

        DefaultFaceTessellator tessellator = new DefaultFaceTessellator(
                new DefaultEdgeDiscretizer(),
                triangulator,
                null,
                GeometryTolerance.defaults()
        );

        ConicalSurface3 cone = new ConicalSurface3(
                new Frame3(
                        new Point3(0.0, 0.0, 0.0),
                        UnitVec3.of(new Vec3(1.0, 0.0, 0.0)),
                        UnitVec3.of(new Vec3(0.0, 1.0, 0.0)),
                        UnitVec3.of(new Vec3(0.0, 0.0, 1.0))
                ),
                10.0,
                Math.toRadians(10.0)
        );

        LoopGeom loop = rectangularLoopOnCone(cone, "#loop1");
        FaceGeom face = new FaceGeom("#f1", cone, List.of(loop), true);

        FaceMeshPatch patch = assertDoesNotThrow(() -> tessellator.tessellate(face));

        assertNotNull(patch);
        assertEquals(4, patch.vertices().size());
        assertEquals(2, patch.triangles().size());
    }




















    private LoopGeom rectangularLoopOnCone(ConicalSurface3 cone, String loopId) {
        Point3 p0 = cone.pointAt(0.0, 0.0);
        Point3 p1 = cone.pointAt(0.5, 0.0);
        Point3 p2 = cone.pointAt(0.5, 10.0);
        Point3 p3 = cone.pointAt(0.0, 10.0);

        return new LoopGeom(loopId, List.of(
                orientedEdge("#oe1", "#e1", p0, p1),
                orientedEdge("#oe2", "#e2", p1, p2),
                orientedEdge("#oe3", "#e3", p2, p3),
                orientedEdge("#oe4", "#e4", p3, p0)
        ));
    }

    private OrientedEdgeGeom orientedEdge(String oeId, String eId, Point3 start, Point3 end) {
        EdgeGeom edge = new EdgeGeom(
                eId,
                new LineCurve3(start, UnitVec3.of(end.subtract(start))),
                new VertexGeom(eId + "_v1", start),
                new VertexGeom(eId + "_v2", end),
                true
        );

        return new OrientedEdgeGeom(oeId, edge, true);
    }

}