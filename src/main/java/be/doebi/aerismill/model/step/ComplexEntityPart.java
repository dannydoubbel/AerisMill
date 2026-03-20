package be.doebi.aerismill.model.step;

import java.util.List;

public class ComplexEntityPart {
    private final String type;
    private final List<String> params;

    public ComplexEntityPart(String type, List<String> params) {
        this.type = type;
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public List<String> getParams() {
        return params;
    }
}
