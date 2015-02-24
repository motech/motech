package org.motechproject.mds.json;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDefinitionType;

import java.io.IOException;

/**
 * The <code>EntryWriter</code> class is a wrapper for JsonWriter that provides ability to serialize single
 * entity record to export file. Entity record contains schema and may contain all instances of entity.
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.json.EntityWriter
 * @see org.motechproject.mds.json.InstancesWriter
 * @see com.google.gson.stream.JsonWriter
 * @see org.motechproject.mds.json.EntityReader
 */
public class EntryWriter {

    private JsonWriter jsonWriter;
    private Entity entity;
    private ExportContext exportContext;

    public EntryWriter(JsonWriter jsonWriter, Entity entity, ExportContext exportContext) {
        this.jsonWriter = jsonWriter;
        this.entity = entity;
        this.exportContext = exportContext;
    }

    public void write() throws IOException {
        if (exportContext.getBlueprint().isIncludeEntitySchema(entity.getClassName())) {
            jsonWriter.name("entity").value(entity.getClassName());
            jsonWriter.name("type").value(entity.isDDE() ?
                    EntityDefinitionType.DDE.toString() : EntityDefinitionType.EUDE.toString());

            EntityWriter entityWriter = new EntityWriter(jsonWriter, entity);
            jsonWriter.name("schema");
            entityWriter.write();

            if (exportContext.getBlueprint().isIncludeEntityData(entity.getClassName())) {
                InstancesWriter instancesWriter = new InstancesWriter(jsonWriter, entity, exportContext);
                jsonWriter.name("instances");
                instancesWriter.writeInstances();
            }
        }
    }
}
