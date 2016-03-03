package org.motechproject.mds.json;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.ImportExportBlueprint;

import java.io.IOException;

/**
 * The <code>ExportWriter</code> class is a wrapper for JsonWriter that provides ability to serialize schema and/or
 * instances of selected entities.
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.json.EntryWriter
 * @see com.google.gson.stream.JsonWriter
 * @see org.motechproject.mds.json.ImportReader
 */
public class ExportWriter {

    private JsonWriter jsonWriter;
    private ExportContext exportContext;

    public ExportWriter(JsonWriter jsonWriter, ExportContext exportContext) {

        this.jsonWriter = jsonWriter;
        this.exportContext = exportContext;
    }

    public void export() throws IOException {
        jsonWriter.beginArray();
        for (ImportExportBlueprint.Record record : exportContext.getBlueprint()) {
            EntryWriter entryWriter = beginEntry(record);
            entryWriter.write();
            endEntry();
        }
        jsonWriter.endArray();
    }

    private EntryWriter beginEntry(ImportExportBlueprint.Record record) throws IOException {
        jsonWriter.beginObject();
        Entity entity = exportContext.getEntity(record.getEntityName());
        return new EntryWriter(jsonWriter, entity, exportContext);
    }

    private void endEntry() throws IOException {
        jsonWriter.endObject();
    }
}
