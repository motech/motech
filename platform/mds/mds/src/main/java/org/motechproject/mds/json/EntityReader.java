package org.motechproject.mds.json;

import com.google.gson.stream.JsonReader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.domain.Tracking;

import java.io.IOException;
import java.util.List;
/**
 * The <code>EntityReader</code> class is a wrapper for JsonReader that provides ability to deserialize entity schema
 * from underlying reader that was previously written by EntityWriter.
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.json.FieldReader
 * @see org.motechproject.mds.json.LookupReader
 * @see com.google.gson.stream.JsonReader
 * @see org.motechproject.mds.json.EntityWriter
 */
public class EntityReader {

    private JsonReader jsonReader;
    private Entity entity;
    private FieldReader fieldReader;
    private LookupReader lookupReader;
    private ObjectReader objectReader;

    public EntityReader(JsonReader jsonReader, Entity entity, ImportContext importContext) {
        this.jsonReader = jsonReader;
        this.entity = entity;
        this.fieldReader = new FieldReader(jsonReader, entity, importContext);
        this.lookupReader = new LookupReader(jsonReader, entity);
        this.objectReader = new ObjectReader(jsonReader);
    }

    public Entity readEntity() throws IOException {
        jsonReader.beginObject();
        readFields();
        readLookups();
        readDataBrowsingSettings();
        readRestApiSettings();
        readAuditingSettings();
        jsonReader.endObject();
        return entity;
    }

    private void readAuditingSettings() throws IOException {
        Tracking tracking = new Tracking();
        objectReader.expect("auditing");
        jsonReader.beginObject();
        readAuditingHistory(tracking);
        readAuditingCrudEvents(tracking);
        jsonReader.endObject();
        entity.setTracking(tracking);
    }

    private void readAuditingCrudEvents(Tracking tracking) throws IOException {
        objectReader.expect("events");
        jsonReader.beginObject();
        tracking.setAllowCreateEvent(objectReader.readBoolean("create"));
        tracking.setAllowUpdateEvent(objectReader.readBoolean("update"));
        tracking.setAllowDeleteEvent(objectReader.readBoolean("delete"));
        jsonReader.endObject();
    }

    private void readAuditingHistory(Tracking tracking) throws IOException {
        tracking.setRecordHistory(objectReader.readBoolean("recordHistory"));
    }

    private void readRestApiSettings() throws IOException {
        objectReader.expect("rest");
        jsonReader.beginObject();
        readRestApiFields();
        readRestApiActions();
        readRestApiLookups();
        jsonReader.endObject();
    }

    private void readRestApiLookups() throws IOException {
        List<String> restLookupsNames = objectReader.readStringArray("lookups");
        for (Lookup lookup : entity.getLookups()) {
            lookup.setExposedViaRest(restLookupsNames.contains(lookup.getLookupName()));
        }
    }

    private void readRestApiActions() throws IOException {
        RestOptions restOptions = new RestOptions();
        objectReader.expect("actions");
        jsonReader.beginObject();
        restOptions.setAllowCreate(objectReader.readBoolean("create"));
        restOptions.setAllowRead(objectReader.readBoolean("read"));
        restOptions.setAllowUpdate(objectReader.readBoolean("update"));
        restOptions.setAllowDelete(objectReader.readBoolean("delete"));
        jsonReader.endObject();
        entity.setRestOptions(restOptions);
    }

    private void readRestApiFields() throws IOException {
        List<String> restFieldsNames = objectReader.readStringArray("fields");
        for (Field field : entity.getFields()) {
            field.setExposedViaRest(restFieldsNames.contains(field.getName()));
        }
    }

    private void readDataBrowsingSettings() throws IOException {
        objectReader.expect("browsing");
        jsonReader.beginObject();
        readDataBrowsingDisplayFields();
        readDataBrowsingFilters();
        jsonReader.endObject();
    }

    private void readDataBrowsingDisplayFields() throws IOException {
        List<String> displayFieldsNames = objectReader.readStringArray("fields");
        for (Field field : entity.getFields()) {
            long uiDisplayPosition = displayFieldsNames.indexOf(field.getName());
            field.setUIDisplayable(uiDisplayPosition >= 0);
            field.setUIDisplayPosition(uiDisplayPosition);
        }
    }

    private void readDataBrowsingFilters() throws IOException {
        List<String> filterFieldsNames = objectReader.readStringArray("filters");
        for (Field field : entity.getFields()) {
            field.setUIFilterable(filterFieldsNames.contains(field.getName()));
        }
    }

    private void readFields() throws IOException {
        objectReader.expect("fields");
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            fieldReader.readField();
        }
        jsonReader.endArray();
    }

    private void readLookups() throws IOException {
        objectReader.expect("lookups");
        jsonReader.beginArray();
        while(jsonReader.hasNext()) {
           lookupReader.readLookup();
        }
        jsonReader.endArray();
    }

}
