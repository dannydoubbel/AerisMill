package be.doebi.aerismill.tessellation;

import be.doebi.aerismill.tessellation.face.FaceTessellator;

public class DefaultSolidTessellator /*implements SolidTessellator*/ {

    private final FaceTessellator faceTessellator;
    //private final MeshBuilderFactory meshBuilderFactory;


    DefaultSolidTessellator(FaceTessellator faceTessellator) {
        this.faceTessellator = faceTessellator;
    }

}