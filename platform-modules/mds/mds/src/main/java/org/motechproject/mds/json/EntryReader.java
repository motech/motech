package org.motechproject.mds.json;

import com.google.gson.stream.JsonReader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDefinitionType;
import org.motechproject.mds.domain.ImportExportBlueprint;

import java.io.IOException;

/**
 * The <code>EntryReader</code> class is a wrapper for JsonReader that provides ability to deserialize single
 * entity record from import file. Entity record contains schema and may contain all instances of entity.
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.json.EntityReader
 * @see org.motechproject.mds.json.InstancesReader
 * @see com.google.gson.stream.JsonReader
 * @see org.motechproject.mds.json.EntryWriter
 */
public class EntryReader {

    private JsonReader jsonReader;
    private String entityName;
    private EntityDefinitionType entityType;
    private Entity existingEntity;
    private ImportContext importContext;
    private ObjectReader objectReader;

    public EntryReader(JsonReader jsonReader, String entityName, EntityDefinitionType entityType, Entity existingEntity, ImportContext importContext) {
        this.jsonReader = jsonReader;
        this.entityName = entityName;
        this.entityType = entityType;
        this.existingEntity = existingEntity;
        this.importContext = importContext;
        this.objectReader = new ObjectReader(jsonReader);
    }

    public boolean isSchemaIncluded() {
        return importContext.getBlueprint().isIncludeEntitySchema(entityName);
    }

    public boolean isDataIncluded(ImportExportBlueprint blueprint) {
        return blueprint.isIncludeEntityData(entityName);
    }

    public Entity readSchema() throws IOException {
        objectReader.expect("schema");
        Entity entity = null != existingEntity ? importContext.setupExistingEntity(existingEntity) : importContext.setupNewEntity(entityName);
        EntityReader entityReader = new EntityReader(jsonReader, entity, importContext);
        return entityReader.readEntity();
    }

    public void skipSchema() throws IOException {
        objectReader.expectAndSkip("schema");
    }

    public void skipData() throws IOException {
        objectReader.expectAndSkipIfExists("instances");
    }

    public void skip() throws IOException {
        skipSchema();
        skipData();
    }

    public InstancesReader beginInstances() throws IOException {
        objectReader.expect("instances");
        jsonReader.beginArray();
        return new InstancesReader(jsonReader, existingEntity, importContext);
    }

    public void endInstances() throws IOException {
        jsonReader.endArray();
    }

    public boolean isImportable() {
        if (EntityDefinitionType.DDE.equals(entityType)) {
            return null != existingEntity && existingEntity.isDDE();
        } else {
            return null == existingEntity || !existingEntity.isDDE();
        }
    }

    public boolean isResolved() {
        for (Entity entity : importContext.getUnresolvedEntities()) {
            if (entityName.equals(entity.getClassName())) {
                return false;
            }
        }
        return true;
    }
}
