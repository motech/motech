package org.motechproject.mds.json;

import com.google.gson.stream.JsonReader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDefinitionType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>ImportReader</code> class is a wrapper for JsonReader that provides ability to deserialize schema and/or
 * instances of selected entities.
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.json.EntityReader
 * @see com.google.gson.stream.JsonReader
 * @see org.motechproject.mds.json.ExportWriter
 */
public class ImportReader {

    private JsonReader jsonReader;
    private ObjectReader objectReader;
    private ImportContext importContext;

    public ImportReader(JsonReader jsonReader, ImportContext importContext) {
        this.jsonReader = jsonReader;
        this.importContext = importContext;
        this.objectReader = new ObjectReader(jsonReader);
    }

    public void begin() throws IOException {
        jsonReader.beginArray();
    }

    public void end() throws IOException {
        jsonReader.endArray();
    }

    public boolean hasNext() throws IOException {
        return jsonReader.hasNext();
    }

    public EntryReader beginEntry() throws IOException {
        jsonReader.beginObject();
        String entityName = objectReader.readString("entity");
        EntityDefinitionType entityType = objectReader.readEnum("type", EntityDefinitionType.class);
        Entity entity = importContext.getEntity(entityName);
        return new EntryReader(jsonReader, entityName, entityType, entity, importContext);
    }

    public void endEntry() throws IOException {
        jsonReader.endObject();
    }

    public void importSchema() throws IOException {
        Set<Entity> entities = new HashSet<>();
        jsonReader.beginArray();
        while (hasNext()) {
            EntryReader entryReader = beginEntry();
            if (entryReader.isSchemaIncluded() && entryReader.isImportable()) {
                entities.add(entryReader.readSchema());
                entryReader.skipData();
            } else {
                entryReader.skip();
            }
            endEntry();
        }
        jsonReader.endArray();
        importContext.setEntities(entities);
        importContext.persistResolvedEntities();
    }

    public void importInstances() throws IOException {
        jsonReader.beginArray();
        while (hasNext()) {
            EntryReader entryReader = beginEntry();
            if (entryReader.isDataIncluded(importContext.getBlueprint()) && entryReader.isImportable() && entryReader.isResolved()) {
                entryReader.skipSchema();
                InstancesReader instancesReader = entryReader.beginInstances();
                while (instancesReader.hasNext()) {
                    instancesReader.importInstance();
                }
                entryReader.endInstances();
            } else {
                entryReader.skip();
            }
            endEntry();
        }
        jsonReader.endArray();
    }
}
