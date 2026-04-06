package be.doebi.aerismill.fx.viewer;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshBounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.PerspectiveCamera;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class MeshViewerPane extends StackPane {

    private final Group sceneRoot = new Group();
    private final Group worldRoot = new Group();
    private final Group modelPivot = new Group();
    private final Group modelContent = new Group();
    private final Group cameraHolder = new Group();

    private final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final AmbientLight ambientLight = new AmbientLight(Color.color(0.35, 0.35, 0.35));
    private final PointLight headLight = new PointLight(Color.WHITE);

    private final SubScene subScene;
    private final MeshViewFactory meshViewFactory;


    private double anchorX;
    private double anchorY;
    private double anchorAngleX;
    private double anchorAngleY;

    private double cameraDistance = 500.0;
    private double minCameraDistance = 1.0;
    private double maxCameraDistance = 1_000_000.0;

    private double anchorPanTranslateX;
    private double anchorPanTranslateY;

    private final Label overlayLabel = new Label();

    private final Group panGroup = new Group();

    public MeshViewerPane() {
        this(new MeshViewFactory());
    }

    public MeshViewerPane(MeshViewFactory meshViewFactory) {
        this.meshViewFactory = Objects.requireNonNull(meshViewFactory, "meshViewFactory must not be null");

        modelPivot.getTransforms().addAll(rotateX, rotateY);
        modelPivot.getChildren().add(modelContent);

        panGroup.getChildren().add(modelPivot);

        cameraHolder.getChildren().addAll(camera, headLight);

        sceneRoot.getChildren().addAll(worldRoot, cameraHolder);
        worldRoot.getChildren().addAll(panGroup, ambientLight);

        subScene = new SubScene(
                sceneRoot,
                800,
                600,
                true,
                SceneAntialiasing.BALANCED
        );
        subScene.setFill(Color.rgb(30, 30, 30));
        subScene.setCamera(camera);

        subScene.widthProperty().bind(widthProperty());
        subScene.heightProperty().bind(heightProperty());

        getChildren().add(subScene);

        overlayLabel.setMouseTransparent(true);
        overlayLabel.setStyle("""
        -fx-background-color: rgba(20,20,20,0.75);
        -fx-text-fill: white;
        -fx-padding: 8 10 8 10;
        -fx-background-radius: 6;
        -fx-font-family: Consolas;
        -fx-font-size: 12px;
        """);

        getChildren().add(overlayLabel);
        StackPane.setAlignment(overlayLabel, Pos.TOP_LEFT);
        StackPane.setMargin(overlayLabel, new Insets(10));

        configureCamera();
        configureMouseControls();
    }

    public void setMesh(Mesh mesh) {
        Objects.requireNonNull(mesh, "mesh must not be null");

        MeshView meshView = meshViewFactory.create(mesh);
        modelContent.getChildren().setAll(meshView);

        resetView();
        fitToMesh(mesh.bounds());
        updateOverlay(mesh);
    }

    private void updateOverlay(Mesh mesh) {
        MeshBounds bounds = mesh.bounds();

        overlayLabel.setText(
                "Vertices : " + mesh.vertexCount() + System.lineSeparator() +
                        "Triangles: " + mesh.triangleCount() + System.lineSeparator() +
                        "Size     : " + format(bounds.sizeX()) + " x "
                        + format(bounds.sizeY()) + " x "
                        + format(bounds.sizeZ()) + System.lineSeparator() +
                        "Diagonal : " + format(bounds.diagonal())
        );
    }

    private String format(double value) {
        return String.format(java.util.Locale.US, "%.3f", value);
    }

    public void resetView() {
        rotateX.setAngle(-20);
        rotateY.setAngle(-20);
        panGroup.setTranslateX(0);
        panGroup.setTranslateY(0);
    }

    private void configureCamera() {
        camera.setNearClip(0.1);
        camera.setFarClip(100_000.0);
        applyCameraDistance();
    }

    private void configureMouseControls() {
        subScene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();

            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();

            anchorPanTranslateX = panGroup.getTranslateX();
            anchorPanTranslateY = panGroup.getTranslateY();
        });

        subScene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - anchorX;
            double deltaY = event.getSceneY() - anchorY;

            if (event.isMiddleButtonDown()) {
                panGroup.setTranslateX(anchorPanTranslateX + deltaX);
                panGroup.setTranslateY(anchorPanTranslateY + deltaY);
                return;
            }

            if (event.isPrimaryButtonDown()) {
                rotateY.setAngle(anchorAngleY + deltaX * 0.5);
                rotateX.setAngle(anchorAngleX - deltaY * 0.5);
            }
        });

        subScene.setOnScroll(event -> {
            if (event.getDeltaY() == 0.0) {
                return;
            }

            double zoomFactor = event.getDeltaY() > 0 ? 0.9 : 1.1;
            cameraDistance *= zoomFactor;
            cameraDistance = clamp(cameraDistance, minCameraDistance, maxCameraDistance);

            applyCameraDistance();
        });
    }

    private void fitToMesh(MeshBounds bounds) {
        Point3 center = bounds.center();

        modelContent.setTranslateX(-center.x());
        modelContent.setTranslateY(-center.y());
        modelContent.setTranslateZ(-center.z());

        double diagonal = bounds.diagonal();
        if (diagonal <= 0.0) {
            diagonal = 10.0;
        }

        cameraDistance = diagonal * 2.0;
        minCameraDistance = Math.max(diagonal * 0.05, 0.1);
        maxCameraDistance = Math.max(diagonal * 50.0, 100.0);

        camera.setNearClip(Math.max(diagonal / 10_000.0, 0.01));
        camera.setFarClip(Math.max(diagonal * 100.0, 1_000.0));

        applyCameraDistance();
    }

    private void applyCameraDistance() {
        cameraHolder.setTranslateZ(-cameraDistance);
    }

    public void clear() {
        modelContent.getChildren().clear();
        modelContent.setTranslateX(0);
        modelContent.setTranslateY(0);
        modelContent.setTranslateZ(0);
        overlayLabel.setText("");
        resetView();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}