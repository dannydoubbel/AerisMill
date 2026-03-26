package be.doebi.aerismill.model.step.core;

import be.doebi.aerismill.model.step.base.StepEntity;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StepModel {
    private File sourceFile;
    private String fileName;
    private String rawContent;
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

    public Collection<StepEntity> getAllEntities() {
        return Collections.unmodifiableCollection(entitiesById.values());
    }

    public int getEntityCount() {
        return entitiesById.size();
    }

    public Map<String, StepEntity> getEntitiesById() {
        return Collections.unmodifiableMap(entitiesById);
    }

    public void setSourceFile(File sourceFile) { this.sourceFile = sourceFile; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }

}