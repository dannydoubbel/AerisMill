package be.doebi.aerismill.eval.step.placement;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;

public interface PlacementEvaluator {
    Point3 evaluatePoint(CartesianPoint point);
    UnitVec3 evaluateDirection(Direction direction);
    Vec3 evaluateVector(Vector vector);
    Frame3 evaluateAxis2Placement3D(Axis2Placement3D placement);
}