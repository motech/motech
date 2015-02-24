package org.motechproject.mds.json;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;

import java.io.IOException;

/**
 * The <code>EntityWriter</code> class is a wrapper for JsonWriter that provides ability to serialize entity schema
 * to underlying writer. Generated schema contains all necessary information that are needed to define an entity.
 * <p>
 * Generated json format:
 * <p><pre>{@code
 *   {
 *     "fields": [ {{fields definition}} ],
 *     "lookups": [ {{lookups defifition}} ],
 *     "browsing": {
 *       "fields": [ {{displayable fields names in correct order}} ],
 *       "filters": [ {{filterable fields names}} ]
 *     },
 *     "rest": {
 *       "fields": [ {{REST exposed fields names}} ],
 *       "actions": {
 *         "create": {{true if CREATE action should be enabled via REST API, false otherwise}},
 *         "read": {{true if READ action should be enabled via REST API, false otherwise}},
 *         "update": {{true if UPDATE action should be enabled via REST API, false otherwise}},
 *         "delete": {{true if DELETE action should be enabled via REST API, false otherwise}}
 *       },
 *       "lookups": [ {{REST exposed lookups names}} ]
 *     },
 *     "auditing": {
 *       "recordHistory": {{true if history recording should be enabled, false otherwise}},
 *       "events": {
 *         "create": {{true if event should be send after instance creation, false otherwise}},
 *         "update": {{true if event should be send after instance update, false otherwise}},
 *         "delete": {{true if event should be send after instance deletion, false otherwise}}
 *       }
 *     }
 *   }
 * }</pre>
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.json.FieldWriter
 * @see org.motechproject.mds.json.LookupWriter
 * @see com.google.gson.stream.JsonWriter
 * @see org.motechproject.mds.json.EntityReader
 */
public class EntityWriter {

    private JsonWriter jsonWriter;
    private FieldWriter fieldWriter;
    private LookupWriter lookupWriter;
    private Entity entity;

    public EntityWriter(JsonWriter jsonWriter, Entity entity) {
        this.jsonWriter = jsonWriter;
        this.entity = entity;
        this.fieldWriter = new FieldWriter(jsonWriter);
        this.lookupWriter = new LookupWriter(jsonWriter);
    }

    public void write() throws IOException {
        jsonWriter.beginObject();
        writeFields();
        writeLookups();
        writeDataBrowsingSettings();
        writeRestApiSettings();
        writeAuditingSettings();
        jsonWriter.endObject();
    }

    private void writeFields() throws IOException {
        jsonWriter.name("fields");
        jsonWriter.beginArray();
        for (Field field : entity.getFields()) {
            if (!field.isReadOnly()) {
                fieldWriter.writeField(field);
            }
        }
        jsonWriter.endArray();
    }

    private void writeLookups() throws IOException {
        jsonWriter.name("lookups");
        jsonWriter.beginArray();
        for (Lookup lookup : entity.getLookups()) {
            if (!lookup.isReadOnly()) {
                lookupWriter.writeLookup(lookup);
            }
        }
        jsonWriter.endArray();
    }

    private void writeAuditingSettings() throws IOException {
        jsonWriter.name("auditing");
        jsonWriter.beginObject();
        writeAuditingHistory();
        writeAuditingCrudEvents();
        jsonWriter.endObject();
    }

    private void writeAuditingCrudEvents() throws IOException {
        jsonWriter.name("events");
        jsonWriter.beginObject();
        jsonWriter.name("create").value(entity.isAllowCreateEvent());
        jsonWriter.name("update").value(entity.isAllowUpdateEvent());
        jsonWriter.name("delete").value(entity.isAllowDeleteEvent());
        jsonWriter.endObject();
    }

    private void writeAuditingHistory() throws IOException {
        jsonWriter.name("recordHistory").value(entity.isRecordHistory());
    }

    private void writeRestApiSettings() throws IOException {
        jsonWriter.name("rest");
        jsonWriter.beginObject();
        RestOptions restOptions = null != entity.getRestOptions() ? entity.getRestOptions() : new RestOptions(entity);
        writeRestApiFields(restOptions);
        writeRestApiActions(restOptions);
        writeRestApiLookups(restOptions);
        jsonWriter.endObject();
    }

    private void writeRestApiLookups(RestOptions restOptions) throws IOException {
        lookupWriter.writeLookupNamesArray("lookups", restOptions.getLookups());
    }

    private void writeRestApiActions(RestOptions restOptions) throws IOException {
        jsonWriter.name("actions");
        jsonWriter.beginObject();
        jsonWriter.name("create").value(restOptions.isAllowCreate());
        jsonWriter.name("read").value(restOptions.isAllowRead());
        jsonWriter.name("update").value(restOptions.isAllowUpdate());
        jsonWriter.name("delete").value(restOptions.isAllowDelete());
        jsonWriter.endObject();
    }

    private void writeRestApiFields(RestOptions restOptions) throws IOException {
        fieldWriter.writeFieldNamesArray("fields", restOptions.getFields());
    }

    private void writeDataBrowsingSettings() throws IOException {
        jsonWriter.name("browsing");
        jsonWriter.beginObject();
        writeDataBrowsingDisplayFields();
        writeDataBrowsingFilters();
        jsonWriter.endObject();
    }

    private void writeDataBrowsingFilters() throws IOException {
        fieldWriter.writeFieldNamesArray("filters", entity.getBrowsingSettings().getFilterableFields());
    }

    private void writeDataBrowsingDisplayFields() throws IOException {
        fieldWriter.writeFieldNamesArray("fields", entity.getBrowsingSettings().getDisplayedFields());
    }
}
