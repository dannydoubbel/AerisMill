package be.doebi.aerismill.model.step;

public class StepEntity {
    private final String id;
    private final String type;
    private final String rawParameters;

    public StepEntity(String id, String type, String rawParameters) {
        this.id = id;
        this.type = type;
        this.rawParameters = rawParameters;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRawParameters() {
        return rawParameters;
    }

    @Override
    public String toString() {
        return "StepEntity{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", rawParameters='" + rawParameters + '\'' +
                '}';
    }
}
