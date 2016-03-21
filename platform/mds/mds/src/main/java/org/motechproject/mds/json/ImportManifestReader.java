package org.motechproject.mds.json;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDefinitionType;
import org.motechproject.mds.domain.ImportManifest;
import org.motechproject.mds.repository.internal.AllEntities;

import java.io.IOException;

/**
 * The <code>ImportManifestReader</code> is a wrapper for JsonReader that provides methods for extracting
 * ImportManifest from underlying reader.
 *
 * @see org.motechproject.mds.domain.ImportManifest
 * @see org.motechproject.mds.json.ExportWriter
 * @see org.motechproject.mds.json.ImportReader
 */
public class ImportManifestReader {

    private JsonReader jsonReader;
    private AllEntities allEntities;
    private ObjectReader objectReader;

    public ImportManifestReader(JsonReader jsonReader, AllEntities allEntities) {
        this.jsonReader = jsonReader;
        this.allEntities = allEntities;
        this.objectReader = new ObjectReader(jsonReader);
    }

    public ImportManifest readManifest() throws IOException {
        ImportManifest manifest = new ImportManifest();

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();

            String entityName = objectReader.readString("entity");
            EntityDefinitionType entityType = objectReader.readEnum("type", EntityDefinitionType.class);
            Entity existingEntity = getExistingEntity(entityName);

            boolean canImport = isImportable(existingEntity, entityType);
            String moduleName = null != existingEntity && existingEntity.isDDE() ? existingEntity.getModule() : "MDS";

            ImportManifest.Record manifestRecord = manifest.addRecord(entityName, moduleName);

            objectReader.expectAndSkip("schema");
            manifestRecord.setCanIncludeSchema(canImport);

            if (jsonReader.hasNext()) {
                objectReader.expectAndSkip("instances");
                manifestRecord.setCanIncludeData(canImport);
            }

            if (jsonReader.hasNext()) {
                throw new JsonParseException("Invalid json format! Unexpected property: " + jsonReader.nextName());
            }

            jsonReader.endObject();
        }
        jsonReader.endArray();

        return manifest;
    }

    public boolean isImportable(Entity existingEntity, EntityDefinitionType entityType) {
        if (EntityDefinitionType.DDE.equals(entityType)) {
            return null != existingEntity && existingEntity.isDDE();
        } else {
            return null == existingEntity || !existingEntity.isDDE();
        }
    }

    private Entity getExistingEntity(String entityName) {
        return allEntities.retrieveByClassName(entityName);
    }
}
