package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;

public class DefaultTopologyValidationService implements TopologyValidationService {

    private final DefaultLoopGeomValidator loopValidator;
    private final FaceGeomValidator faceValidator;
    private final ShellGeomValidator shellValidator;
    private final SolidGeomValidator solidValidator;

    public DefaultTopologyValidationService() {
        this.loopValidator = new DefaultLoopGeomValidator();
        this.faceValidator = new DefaultFaceGeomValidator(loopValidator);
        this.shellValidator = new DefaultShellGeomValidator(faceValidator);
        this.solidValidator = new DefaultSolidGeomValidator(shellValidator);
    }

    public DefaultTopologyValidationService(
            DefaultLoopGeomValidator loopValidator,
            FaceGeomValidator faceValidator,
            ShellGeomValidator shellValidator,
            SolidGeomValidator solidValidator
    ) {
        this.loopValidator = loopValidator;
        this.faceValidator = faceValidator;
        this.shellValidator = shellValidator;
        this.solidValidator = solidValidator;
    }

    @Override
    public ValidationReport validateLoop(LoopGeom loop) {
        return loopValidator.validate(loop);
    }

    @Override
    public ValidationReport validateFace(FaceGeom face) {
        return faceValidator.validate(face);
    }

    @Override
    public ValidationReport validateShell(ShellGeom shell) {
        return shellValidator.validate(shell);
    }

    @Override
    public ValidationReport validateSolid(SolidGeom solid) {
        return solidValidator.validate(solid);
    }
}