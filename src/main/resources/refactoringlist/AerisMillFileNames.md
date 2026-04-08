# AerisMill Refactoring Manifest

Semantic-safe cleanup manifest for the IntelliJ ChatGPT assistant.

## Scope

- Included: Java production files under `src/main/java`.
- Excluded for now: all tests under `src/test/java`, all `*Test.java`, all `*IT.java`, test fixtures/stubs/utilities, and non-Java resources unless explicitly requested.
- Production files currently listed: **265**
- Package sections currently listed: **50**

## Status Legend

- [ ] Pending
- [x] Done
- [!] Risky / Skipped
- [-] Excluded for now

## Batch Rules

1. Process **at most 5 files per run**.
2. Only touch files marked `[ ]`.
3. If a file is risky, do not edit it; mark it `[!]` and explain why.
4. After each batch, update this manifest and stop.
5. Keep changes semantics-preserving only: imports, formatting, field order, constructor order, method grouping, tiny dead private helpers only if provably safe.

## High-Caution Areas

- JavaFX UI/controller files
- Machine / serial / polling / connection code
- Service orchestration classes
- Parser registry / dispatch classes
- Evaluator dispatch / topology / tessellation pipeline classes
- `module-info.java`

## Excluded For Now

- [-] `src/test/java/**`
- [-] `src/test/resources/**`
- [-] `**/*Test.java`
- [-] `**/*IT.java`
- [-] `src/main/resources/**` (unless explicitly requested)

## src/main/java

- [!] `src/main/java/module-info.java`

## src/main/java/be/doebi/aerismill/assemble/step/geom

- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/AssembledSolidResult.java`
- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/AssemblyIssue.java`
- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/AssemblyIssueCode.java`
- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/AssemblyIssueSeverity.java`
- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/AssemblyResult.java`
- [!I] `src/main/java/be/doebi/aerismill/assemble/step/geom/DefaultStepToGeomAssembler.java`
- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/SolidAssemblyResult.java`
- [x] `src/main/java/be/doebi/aerismill/assemble/step/geom/StepToGeomAssembler.java`

## src/main/java/be/doebi/aerismill/eval/step/context

- [x] `src/main/java/be/doebi/aerismill/eval/step/context/StepEvaluationCache.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/context/StepEvaluationContext.java`

## src/main/java/be/doebi/aerismill/eval/step/curve

- [x] `src/main/java/be/doebi/aerismill/eval/step/curve/CurveEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/curve/DefaultCurveEvaluator.java`

## src/main/java/be/doebi/aerismill/eval/step/placement

- [!I] `src/main/java/be/doebi/aerismill/eval/step/placement/DefaultPlacementEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/placement/PlacementEvaluator.java`

## src/main/java/be/doebi/aerismill/eval/step/representation

- [x] `src/main/java/be/doebi/aerismill/eval/step/representation/AdvancedBrepShapeRepresentationEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/representation/DefaultAdvancedBrepShapeRepresentationEvaluator.java`

## src/main/java/be/doebi/aerismill/eval/step/surface

- [!I] `src/main/java/be/doebi/aerismill/eval/step/surface/DefaultSurfaceEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/surface/SurfaceEvaluator.java`

## src/main/java/be/doebi/aerismill/eval/step/topology

- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultEdgeGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultFaceGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultLoopGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultManifoldSolidBrepEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultOrientedEdgeGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultShellGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultSolidGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultSolidWithVoidsGeomEvaluator.java`
- [!I] `src/main/java/be/doebi/aerismill/eval/step/topology/DefaultVertexGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/EdgeGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/FaceGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/LoopGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/ManifoldSolidBrepEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/OrientedEdgeGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/ShellGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/SolidGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/SolidWithVoidsGeomEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/TopologyEvaluator.java`
- [x] `src/main/java/be/doebi/aerismill/eval/step/topology/VertexGeomEvaluator.java`

## src/main/java/be/doebi/aerismill/fx/viewer

- [!] `src/main/java/be/doebi/aerismill/fx/viewer/FxTriangleMeshConverter.java`
- [!] `src/main/java/be/doebi/aerismill/fx/viewer/MeshViewerPane.java`
- [!] `src/main/java/be/doebi/aerismill/fx/viewer/MeshViewFactory.java`

## src/main/java/be/doebi/aerismill/io

- [x] `src/main/java/be/doebi/aerismill/io/FileExtensionHelper.java`
- [x] `src/main/java/be/doebi/aerismill/io/ResourcePathHelper.java`

## src/main/java/be/doebi/aerismill/io/step

- [x] `src/main/java/be/doebi/aerismill/io/step/StepReader.java`

## src/main/java/be/doebi/aerismill/io/stl

- [!I] `src/main/java/be/doebi/aerismill/io/stl/AsciiStlReader.java`
- [!I] `src/main/java/be/doebi/aerismill/io/stl/BinaryStlReader.java`

## src/main/java/be/doebi/aerismill/machine

- [!I] `src/main/java/be/doebi/aerismill/machine/GrblStatusParser.java`
- [x] `src/main/java/be/doebi/aerismill/machine/MachineStatus.java`

## src/main/java/be/doebi/aerismill/machine/connection

- [x] `src/main/java/be/doebi/aerismill/machine/connection/BaudRate.java`
- [!I] `src/main/java/be/doebi/aerismill/machine/connection/SerialConnection.java`

## src/main/java/be/doebi/aerismill/machine/grbl

- [!I] `src/main/java/be/doebi/aerismill/machine/grbl/GrblClient.java`

## src/main/java/be/doebi/aerismill/model/geom/curve

- [x] `src/main/java/be/doebi/aerismill/model/geom/curve/BoundedCurve3.java`
- [!I] `src/main/java/be/doebi/aerismill/model/geom/curve/BSplineCurve3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/curve/CircleCurve3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/curve/Curve3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/curve/EllipseCurve3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/curve/LineCurve3.java`

## src/main/java/be/doebi/aerismill/model/geom/math

- [x] `src/main/java/be/doebi/aerismill/model/geom/math/Frame3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/math/Point3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/math/Transform3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/math/UnitVec3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/math/Vec3.java`

## src/main/java/be/doebi/aerismill/model/geom/representation

- [x] `src/main/java/be/doebi/aerismill/model/geom/representation/EvaluatedBrepRepresentation.java`

## src/main/java/be/doebi/aerismill/model/geom/surface

- [!I] `src/main/java/be/doebi/aerismill/model/geom/surface/BSplineSurface3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/ConicalSurface3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/CylindricalSurface3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/PlaneSurface3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/SphericalSurface3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/Surface3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/SurfaceOfRevolution3.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/surface/ToroidalSurface3.java`

## src/main/java/be/doebi/aerismill/model/geom/tolerance

- [x] `src/main/java/be/doebi/aerismill/model/geom/tolerance/GeometryTolerance.java`

## src/main/java/be/doebi/aerismill/model/geom/topology

- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/EdgeGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/FaceGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/LoopGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/OrientedEdgeGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/ShellGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/SolidGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/SolidWithVoidsGeom.java`
- [x] `src/main/java/be/doebi/aerismill/model/geom/topology/VertexGeom.java`

## src/main/java/be/doebi/aerismill/model/mesh

- [x] `src/main/java/be/doebi/aerismill/model/mesh/Mesh.java`
- [x] `src/main/java/be/doebi/aerismill/model/mesh/MeshBounds.java`
- [x] `src/main/java/be/doebi/aerismill/model/mesh/MeshBoundsCalculator.java`
- [x] `src/main/java/be/doebi/aerismill/model/mesh/MeshBuilder.java`
- [x] `src/main/java/be/doebi/aerismill/model/mesh/MeshTriangle.java`
- [x] `src/main/java/be/doebi/aerismill/model/mesh/MeshVertex.java`
- [x] `src/main/java/be/doebi/aerismill/model/mesh/Triangle3.java`

## src/main/java/be/doebi/aerismill/model/step

- [x] `src/main/java/be/doebi/aerismill/model/step/ComplexEntity.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/ComplexEntityPart.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/GeometricEntity.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/TopologyEntity.java`

## src/main/java/be/doebi/aerismill/model/step/admin

- [x] `src/main/java/be/doebi/aerismill/model/step/admin/AdministrativeStepEntity.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ApplicationContext.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ApplicationProtocolDefinition.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/Approval.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ApprovalDateTime.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ApprovalPersonOrganization.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ApprovalRole.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ApprovalStatus.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/CalendarDate.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/DateAndTime.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/DateTimeRole.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/Document.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/DocumentType.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/LocalTime.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/Organization.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/Person.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/PersonalAddress.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/PersonAndOrganization.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/PersonAndOrganizationRole.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ProductCategory.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/ProductCategoryRelationship.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/SecurityClassification.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/admin/SecurityClassificationLevel.java`

## src/main/java/be/doebi/aerismill/model/step/base

- [!I] `src/main/java/be/doebi/aerismill/model/step/base/ResolvableStepEntity.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/base/StepEntity.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/base/StepEntityType.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/base/StepLogical.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/base/StepModel.java`

## src/main/java/be/doebi/aerismill/model/step/core

- [x] `src/main/java/be/doebi/aerismill/model/step/core/StepContext.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/core/StepDataSection.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/core/StepHeader.java`

## src/main/java/be/doebi/aerismill/model/step/definition

- [x] `src/main/java/be/doebi/aerismill/model/step/definition/ProductDefinitionShape.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/definition/ShapeDefinitionRepresentation.java`

## src/main/java/be/doebi/aerismill/model/step/geometry

- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Axis1Placement.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Axis2Placement3D.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/BSplineCurveWithKnots.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/BSplineSurfaceWithKnots.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/CartesianPoint.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Circle.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/ConicalSurface.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/CylindricalSurface.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Direction.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Ellipse.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Line.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Plane.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/SphericalSurface.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/SurfaceOfRevolution.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/ToroidalSurface.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/geometry/Vector.java`

## src/main/java/be/doebi/aerismill/model/step/presentation

- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/ColourRgb.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/CurveStyle.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/DraughtingPreDefinedColour.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/DraughtingPreDefinedCurveFont.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/FillAreaStyle.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/FillAreaStyleColour.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/MechanicalDesignGeometricPresentationRepresentation.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/OverRidingStyledItem.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/PresentationLayerAssignment.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/PresentationStepEntity.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/PresentationStyleAssignment.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/StyledItem.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/SurfaceSideStyle.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/SurfaceStyleFillArea.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/presentation/SurfaceStyleUsage.java`

## src/main/java/be/doebi/aerismill/model/step/representation

- [!I] `src/main/java/be/doebi/aerismill/model/step/representation/AdvancedBrepShapeRepresentation.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/representation/ShapeRepresentation.java`

## src/main/java/be/doebi/aerismill/model/step/resolve

- [x] `src/main/java/be/doebi/aerismill/model/step/resolve/ResolveState.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/resolve/StepResolvable.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/resolve/StepResolveException.java`
- [x] `src/main/java/be/doebi/aerismill/model/step/resolve/StepResolveFailure.java`

## src/main/java/be/doebi/aerismill/model/step/topology

- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/AdvancedFace.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/BrepWithVoids.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/ClosedShell.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/EdgeCurve.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/EdgeLoop.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/FaceBound.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/FaceOuterBound.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/ManifoldSolidBrep.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/OrientedClosedShell.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/OrientedEdge.java`
- [!I] `src/main/java/be/doebi/aerismill/model/step/topology/VertexPoint.java`

## src/main/java/be/doebi/aerismill/parser/step

- [!I] `src/main/java/be/doebi/aerismill/parser/step/ComplexEntityInterpreter.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/ComplexEntityParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/EntityParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/EntityParserRegistry.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/StepParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/StepParserUtils.java`

## src/main/java/be/doebi/aerismill/parser/step/admin

- [!I] `src/main/java/be/doebi/aerismill/parser/step/admin/AdministrativeStepEntityParser.java`

## src/main/java/be/doebi/aerismill/parser/step/definition

- [!I] `src/main/java/be/doebi/aerismill/parser/step/definition/ProductDefinitionShapeParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/definition/ShapeDefinitionRepresentationParser.java`

## src/main/java/be/doebi/aerismill/parser/step/geometry

- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/Axis1PlacementParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/Axis2Placement3DParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/BSplineCurveWithKnotsParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/BSplineSurfaceWithKnotsParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/CartesianPointParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/CircleParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/ConicalSurfaceParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/CylindricalSurfaceParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/DirectionParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/EllipseParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/LineParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/PlaneParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/SphericalSurfaceParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/SurfaceOfRevolutionParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/ToroidalSurfaceParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/geometry/VectorParser.java`

## src/main/java/be/doebi/aerismill/parser/step/presentation

- [!I] `src/main/java/be/doebi/aerismill/parser/step/presentation/PresentationStepEntityParser.java`

## src/main/java/be/doebi/aerismill/parser/step/representation

- [!I] `src/main/java/be/doebi/aerismill/parser/step/representation/AdvancedBrepShapeRepresentationParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/representation/ShapeRepresentationParser.java`

## src/main/java/be/doebi/aerismill/parser/step/topology

- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/AdvancedFaceParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/BrepWithVoidsParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/ClosedShellParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/EdgeCurveParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/EdgeLoopParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/FaceBoundParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/FaceOuterBoundParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/ManifoldSolidBrepParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/OrientedClosedShellParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/OrientedEdgeParser.java`
- [!I] `src/main/java/be/doebi/aerismill/parser/step/topology/VertexPointParser.java`

## src/main/java/be/doebi/aerismill/report/step

- [x] `src/main/java/be/doebi/aerismill/report/step/StepResolveReport.java`

## src/main/java/be/doebi/aerismill/service

- [x] `src/main/java/be/doebi/aerismill/service/AssembledSolidMeshService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/DefaultAssembledSolidMeshService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/DefaultStepAssemblyMeshService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/DefaultStepModelMeshService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/DroPollingService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/MachineControlService.java`
- [x] `src/main/java/be/doebi/aerismill/service/StepAssemblyMeshService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/StepAssemblyService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/StepImportService.java`
- [x] `src/main/java/be/doebi/aerismill/service/StepModelMeshService.java`
- [!I] `src/main/java/be/doebi/aerismill/service/UIStateService.java`

## src/main/java/be/doebi/aerismill/tessellation/curve

- [x] `src/main/java/be/doebi/aerismill/tessellation/curve/CurveDiscretizer.java`
- [!I] `src/main/java/be/doebi/aerismill/tessellation/curve/DefaultEdgeDiscretizer.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/curve/EdgeDiscretizer.java`

## src/main/java/be/doebi/aerismill/tessellation/face

- [x] `src/main/java/be/doebi/aerismill/tessellation/face/FaceMeshPatch.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/face/FaceTessellator.java`
- [!I] `src/main/java/be/doebi/aerismill/tessellation/face/PlanarFaceTessellator.java`

## src/main/java/be/doebi/aerismill/tessellation/polygon

- [!I] `src/main/java/be/doebi/aerismill/tessellation/polygon/EarClippingPolygonTriangulator.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/polygon/Point2.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/polygon/PolygonLoop2.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/polygon/PolygonTriangulator.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/polygon/PolygonWithHoles2.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/polygon/RecordingPolygonTriangulator.java`

## src/main/java/be/doebi/aerismill/tessellation/projection

- [!I] `src/main/java/be/doebi/aerismill/tessellation/projection/DefaultPlaneProjector.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/projection/PlaneProjector.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/projection/RecordingPlaneProjector.java`

## src/main/java/be/doebi/aerismill/tessellation/shell

- [!I] `src/main/java/be/doebi/aerismill/tessellation/shell/DefaultShellTessellator.java`
- [!I] `src/main/java/be/doebi/aerismill/tessellation/shell/PreviewShellTessellator.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/shell/ShellTessellator.java`

## src/main/java/be/doebi/aerismill/tessellation/solid

- [!I] `src/main/java/be/doebi/aerismill/tessellation/solid/DefaultSolidTessellator.java`
- [x] `src/main/java/be/doebi/aerismill/tessellation/solid/SolidTessellator.java`

## src/main/java/be/doebi/aerismill/ui

- [!] `src/main/java/be/doebi/aerismill/ui/AerisMillApplication.java`
- [!] `src/main/java/be/doebi/aerismill/ui/AppConsole.java`
- [!] `src/main/java/be/doebi/aerismill/ui/Launcher.java`
- [!] `src/main/java/be/doebi/aerismill/ui/MachineController.java`
- [!] `src/main/java/be/doebi/aerismill/ui/MainController.java`

## src/main/java/be/doebi/aerismill/validate/geom/topology

- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/DefaultFaceGeomValidator.java`
- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/DefaultLoopGeomValidator.java`
- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/DefaultShellGeomValidator.java`
- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/DefaultSolidGeomValidator.java`
- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/DefaultTopologyValidationService.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/FaceGeomValidator.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/GeomTolerance.java`
- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/LoopGeomValidator.java`
- [!I] `src/main/java/be/doebi/aerismill/validate/geom/topology/Point3Tolerance.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/ShellGeomValidator.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/SolidGeomValidator.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/TopologyValidationService.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/ValidationCode.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/ValidationMessage.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/ValidationReport.java`
- [x] `src/main/java/be/doebi/aerismill/validate/geom/topology/ValidationSeverity.java`

## src/main/java/be/doebi/aerismill/validation/step

- [x] `src/main/java/be/doebi/aerismill/validation/step/StepValidator.java`
