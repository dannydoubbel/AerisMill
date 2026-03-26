package be.doebi.aerismill.model.step.core;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StepModel {
    private File sourceFile;
    private String fileName;
    private String rawContent;
    private StepHeader header;

    private final Map<String, StepEntity> entitiesById = new LinkedHashMap<>();

    public void addEntity(StepEntity entity) {
        if (entity == null) {
            return;
        }
        entitiesById.put(entity.getId(), entity);
    }

    public StepEntity getEntity(String id) {
        return entitiesById.get(id);
    }

    public <T extends StepEntity> T getEntity(String id, Class<T> type) {
        StepEntity entity = entitiesById.get(id);
        if (type.isInstance(entity)) {
            return type.cast(entity);
        }
        return null;
    }

    public Collection<StepEntity> getAllEntities() {
        return Collections.unmodifiableCollection(entitiesById.values());
    }

    public int getEntityCount() {
        return entitiesById.size();
    }

    public Map<String, StepEntity> getEntitiesById() {
        return Collections.unmodifiableMap(entitiesById);
    }

    public boolean containsEntity(String id) {
        return entitiesById.containsKey(id);
    }

    public long countEntitiesOfType(StepEntityType type) {
        return entitiesById.values().stream()
                .filter(entity ->  type.getName().equals(entity.getType()))
                .count();
    }

    public StepHeader getHeader() {
        return header;
    }

    public void setHeader(StepHeader header) {
        this.header = header;
    }



    public void setSourceFile(File sourceFile) { this.sourceFile = sourceFile; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }

    @Override
    public String toString() {
        return "StepModel{entityCount=" + getEntityCount() + "}";
    }

}