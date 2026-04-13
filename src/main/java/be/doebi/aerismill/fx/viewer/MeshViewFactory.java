package be.doebi.aerismill.fx.viewer;

import be.doebi.aerismill.model.mesh.Mesh;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;

import java.util.Objects;

public final class MeshViewFactory {

    private final FxTriangleMeshConverter converter;

    public MeshViewFactory() {
        this(new FxTriangleMeshConverter());
    }

    public MeshViewFactory(FxTriangleMeshConverter converter) {
        this.converter = Objects.requireNonNull(converter, "converter must not be null");
    }

    public MeshView create(Mesh mesh) {
        Objects.requireNonNull(mesh, "mesh must not be null");

        MeshView view = new MeshView(converter.convert(mesh));
        view.setCullFace(CullFace.NONE);
        view.setDrawMode(DrawMode.FILL);
        view.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        return view;
    }
}