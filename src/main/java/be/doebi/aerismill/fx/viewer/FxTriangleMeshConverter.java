package be.doebi.aerismill.fx.viewer;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import javafx.scene.shape.TriangleMesh;

import java.util.Objects;

public final class FxTriangleMeshConverter {

    public TriangleMesh convert(Mesh mesh) {
        Objects.requireNonNull(mesh, "mesh must not be null");

        TriangleMesh fxMesh = new TriangleMesh();

        // JavaFX default vertex format is POINT_TEXCOORD,
        // so we provide one dummy texture coordinate.
        fxMesh.getTexCoords().addAll(0f, 0f);

        for (MeshVertex vertex : mesh.vertices()) {
            Point3 p = vertex.point();
            fxMesh.getPoints().addAll(
                    (float) p.x(),
                    (float) p.y(),
                    (float) p.z()
            );
        }

        for (MeshTriangle triangle : mesh.triangles()) {
            fxMesh.getFaces().addAll(
                    triangle.a(), 0,
                    triangle.b(), 0,
                    triangle.c(), 0
            );
        }

        return fxMesh;
    }
}