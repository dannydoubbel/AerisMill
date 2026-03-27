package be.doebi.aerismill.model.step.base;

import be.doebi.aerismill.model.step.resolve.StepResolvable;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepModel {
    private File sourceFile;
    private String name;
    private String rawContent;
    private final Map<String, StepEntity> entities = new HashMap<>();

    public StepModel() {
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setFileName(String name) {
        this.name = name;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public void addEntity(StepEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Step entity cannot be null");
        }
        entities.put(entity.getId(), entity);
    }

    public StepEntity getEntity(String id) {
        return entities.get(id);
    }

    public boolean contains(String id) {
        return entities.containsKey(id);
    }

    public <T extends StepEntity> T getEntityAs(String id, Class<T> expectedType) {
        StepEntity entity = entities.get(id);

        if (entity == null) {
            throw new StepResolveException("Missing referenced entity: " + id);
        }

        if (!expectedType.isInstance(entity)) {
            throw new StepResolveException(
                    "Referenced entity " + id + " is " + entity.getClass().getSimpleName()
                            + ", expected " + expectedType.getSimpleName()
            );
        }

        return expectedType.cast(entity);
    }

    public <T extends StepEntity> T resolveEntity(String id, Class<T> expectedType) {
        T entity = getEntityAs(id, expectedType);

        if (entity instanceof StepResolvable resolvable) {
            resolvable.resolveReferences(this);
        }

        return entity;
    }

    public <T extends StepEntity> List<T> resolveEntityList(List<String> ids, Class<T> expectedType) {
        List<T> result = new ArrayList<>();

        for (String id : ids) {
            result.add(resolveEntity(id, expectedType));
        }

        return result;
    }

    public void resolveAll() {
        for (StepEntity entity : entities.values()) {
            if (entity instanceof StepResolvable resolvable) {
                resolvable.resolveReferences(this);
            }
        }
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getName() {
        return name;
    }

    public String getRawContent() {
        return rawContent;
    }

    public Map<String, StepEntity> getEntityMap() {
        return entities;
    }

    public List<StepEntity> getEntities() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public String toString() {
        return "StepModel{sourceName='" + name + "'}";
    }

    public String getEntityCount() {
        return String.valueOf(entities.size());
    }
}