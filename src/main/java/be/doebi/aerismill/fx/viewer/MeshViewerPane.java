package be.doebi.aerismill.fx.viewer;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshBounds;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Region;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.control.Label;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
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


    private Mesh currentMesh;
    private MeshView selectedTriangleOverlay;

    private static final PhongMaterial SELECTED_TRIANGLE_MATERIAL =
            new PhongMaterial(Color.YELLOW);

    private static final double HIGHLIGHT_OFFSET_FACTOR = 0.0005;
    private static final double HIGHLIGHT_OFFSET_MIN = 0.01;

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
        setCache(false);
        overlayLabel.setCache(false);
        subScene.setCache(false);

        subScene.widthProperty().bind(widthProperty());
        subScene.heightProperty().bind(heightProperty());

        getChildren().add(subScene);
        setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        setMinSize(200, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);





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

        currentMesh = mesh;
        clearSelection();

        MeshView meshView = meshViewFactory.create(mesh);
        meshView.setOnMouseClicked(event -> {
            PickResult pickResult = event.getPickResult();
            int faceIndex = pickResult.getIntersectedFace();

            if (faceIndex >= 0) {
                selectTriangle(faceIndex);
                event.consume();
            }
        });

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

        subScene.setOnMouseClicked(event -> {
            Object target = event.getTarget();
            if (!(target instanceof MeshView)) {
                clearSelection();
                if (currentMesh != null) {
                    updateOverlay(currentMesh);
                }
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
        currentMesh = null;
        clearSelection();

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


    private void selectTriangle(int triangleIndex) {
        if (currentMesh == null) {
            return;
        }

        if (triangleIndex < 0 || triangleIndex >= currentMesh.triangles().size()) {
            clearSelection();
            updateOverlay(currentMesh);
            return;
        }

        MeshTriangle triangle = currentMesh.triangles().get(triangleIndex);

        MeshVertex va = currentMesh.vertices().get(triangle.a());
        MeshVertex vb = currentMesh.vertices().get(triangle.b());
        MeshVertex vc = currentMesh.vertices().get(triangle.c());

        Point3 a = va.point();
        Point3 b = vb.point();
        Point3 c = vc.point();

        MeshView overlay = createTriangleOverlay(a, b, c);

        clearSelection();
        selectedTriangleOverlay = overlay;
        modelContent.getChildren().add(selectedTriangleOverlay);

        updateOverlay(currentMesh, triangleIndex, triangle);
    }

    private MeshView createTriangleOverlay(Point3 a, Point3 b, Point3 c) {
        double[] normal = computeUnitNormal(a, b, c);

        double diagonal = currentMesh.bounds().diagonal();
        double offset = Math.max(diagonal * HIGHLIGHT_OFFSET_FACTOR, HIGHLIGHT_OFFSET_MIN);

        Point3 oa = offsetPoint(a, normal, offset);
        Point3 ob = offsetPoint(b, normal, offset);
        Point3 oc = offsetPoint(c, normal, offset);

        TriangleMesh fxMesh = new TriangleMesh();
        fxMesh.getTexCoords().addAll(0f, 0f);

        fxMesh.getPoints().addAll(
                (float) oa.x(), (float) oa.y(), (float) oa.z(),
                (float) ob.x(), (float) ob.y(), (float) ob.z(),
                (float) oc.x(), (float) oc.y(), (float) oc.z()
        );

        fxMesh.getFaces().addAll(
                0, 0,
                1, 0,
                2, 0
        );

        MeshView overlay = new MeshView(fxMesh);
        overlay.setCullFace(CullFace.NONE);
        overlay.setDrawMode(DrawMode.FILL);
        overlay.setMaterial(SELECTED_TRIANGLE_MATERIAL);
        overlay.setMouseTransparent(true);

        return overlay;
    }

    private void clearSelection() {
        if (selectedTriangleOverlay != null) {
            modelContent.getChildren().remove(selectedTriangleOverlay);
            selectedTriangleOverlay = null;
        }
    }

    private void updateOverlay(Mesh mesh, int triangleIndex, MeshTriangle triangle) {
        MeshBounds bounds = mesh.bounds();

        overlayLabel.setText(
                "Vertices : " + mesh.vertexCount() + System.lineSeparator() +
                        "Triangles: " + mesh.triangleCount() + System.lineSeparator() +
                        "Size     : " + format(bounds.sizeX()) + " x "
                        + format(bounds.sizeY()) + " x "
                        + format(bounds.sizeZ()) + System.lineSeparator() +
                        "Diagonal : " + format(bounds.diagonal()) + System.lineSeparator() +
                        "Selected : triangle #" + triangleIndex + " [" +
                        triangle.a() + ", " + triangle.b() + ", " + triangle.c() + "]"
        );
    }

    private static Point3 offsetPoint(Point3 point, double[] normal, double offset) {
        return new Point3(
                point.x() + normal[0] * offset,
                point.y() + normal[1] * offset,
                point.z() + normal[2] * offset
        );
    }

    private static double[] computeUnitNormal(Point3 a, Point3 b, Point3 c) {
        double ux = b.x() - a.x();
        double uy = b.y() - a.y();
        double uz = b.z() - a.z();

        double vx = c.x() - a.x();
        double vy = c.y() - a.y();
        double vz = c.z() - a.z();

        double nx = uy * vz - uz * vy;
        double ny = uz * vx - ux * vz;
        double nz = ux * vy - uy * vx;

        double length = Math.sqrt(nx * nx + ny * ny + nz * nz);

        if (length == 0.0) {
            return new double[]{0.0, 0.0, 1.0};
        }

        return new double[]{nx / length, ny / length, nz / length};
    }

    public void fitToView() {
        if (currentMesh == null) {
            return;
        }

        clearSelection();
        resetView();
        fitToMesh(currentMesh.bounds());
        updateOverlay(currentMesh);
    }

    public void zoomIn() {
        zoomByFactor(0.9);
    }

    public void zoomOut() {
        zoomByFactor(1.1);
    }

    private void zoomByFactor(double factor) {
        if (currentMesh == null) {
            return;
        }

        cameraDistance *= factor;
        cameraDistance = clamp(cameraDistance, minCameraDistance, maxCameraDistance);
        applyCameraDistance();
    }

    public void setDebugSurfaceFamilyMeshes(DebugSurfaceFamilyMeshes meshes) {
        Objects.requireNonNull(meshes, "meshes must not be null");

        clearSelection();

        Mesh planarMesh = meshes.planarMesh();
        Mesh cylindricalMesh = meshes.cylindricalMesh();
        Mesh conicalMesh = meshes.conicalMesh();

        Group debugGroup = new Group();

        Mesh combinedMesh = combineNonEmptyMeshes(planarMesh, cylindricalMesh, conicalMesh);
        currentMesh = combinedMesh;

        if (planarMesh != null && !planarMesh.isEmpty()) {
            MeshView planarView = meshViewFactory.create(planarMesh);
            planarView.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
            debugGroup.getChildren().add(planarView);
        }

        if (cylindricalMesh != null && !cylindricalMesh.isEmpty()) {
            MeshView cylindricalView = meshViewFactory.create(cylindricalMesh);
            cylindricalView.setMaterial(new PhongMaterial(Color.MEDIUMPURPLE));
            debugGroup.getChildren().add(cylindricalView);
        }

        if (conicalMesh != null && !conicalMesh.isEmpty()) {
            MeshView conicalView = meshViewFactory.create(conicalMesh);
            conicalView.setMaterial(new PhongMaterial(Color.CYAN));
            debugGroup.getChildren().add(conicalView);
        }

        modelContent.getChildren().setAll(debugGroup);

        if (combinedMesh != null && !combinedMesh.isEmpty()) {
            resetView();
            fitToMesh(combinedMesh.bounds());
            updateOverlayForDebugMeshes(planarMesh, cylindricalMesh, conicalMesh, combinedMesh);
        } else {
            clear();
        }


    }

    private void updateOverlayForDebugMeshes(
            Mesh planarMesh,
            Mesh cylindricalMesh,
            Mesh conicalMesh,
            Mesh combinedMesh
    ) {
        MeshBounds bounds = combinedMesh.bounds();

        overlayLabel.setText(
                "Vertices : " + combinedMesh.vertexCount() + System.lineSeparator() +
                        "Triangles: " + combinedMesh.triangleCount() + System.lineSeparator() +
                        "Size     : " + format(bounds.sizeX()) + " x "
                        + format(bounds.sizeY()) + " x "
                        + format(bounds.sizeZ()) + System.lineSeparator() +
                        "Diagonal : " + format(bounds.diagonal()) + System.lineSeparator() +
                        "Planar   : " + triangleCountOf(planarMesh) + System.lineSeparator() +
                        "Cylindrical: " + triangleCountOf(cylindricalMesh) + System.lineSeparator() +
                        "Conical  : " + triangleCountOf(conicalMesh)
        );
    }

    private int triangleCountOf(Mesh mesh) {
        return mesh == null ? 0 : mesh.triangleCount();
    }

    private Mesh combineNonEmptyMeshes(Mesh... meshes) {
        java.util.List<MeshVertex> combinedVertices = new java.util.ArrayList<>();
        java.util.List<MeshTriangle> combinedTriangles = new java.util.ArrayList<>();

        for (Mesh mesh : meshes) {
            if (mesh == null || mesh.isEmpty()) {
                continue;
            }

            int vertexOffset = combinedVertices.size();

            for (MeshVertex vertex : mesh.vertices()) {
                combinedVertices.add(new MeshVertex(
                        combinedVertices.size(),
                        vertex.point()
                ));
            }

            for (MeshTriangle triangle : mesh.triangles()) {
                combinedTriangles.add(new MeshTriangle(
                        triangle.a() + vertexOffset,
                        triangle.b() + vertexOffset,
                        triangle.c() + vertexOffset
                ));
            }
        }

        if (combinedVertices.isEmpty() || combinedTriangles.isEmpty()) {
            return null;
        }

        return new Mesh(combinedVertices, combinedTriangles);
    }

}