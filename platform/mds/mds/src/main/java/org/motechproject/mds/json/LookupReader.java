package org.motechproject.mds.json;

import com.google.gson.stream.JsonReader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.exception.lookup.LookupReadOnlyException;
import org.motechproject.mds.exception.field.FieldNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>LookupReader</code> class is a wrapper for JsonReader that provides ability to deserialize lookup definition
 * from underlying reader. Lookup definition was previously written by LookupWriter.
 *
 * @see org.motechproject.mds.domain.Lookup
 * @see org.motechproject.mds.json.EntityReader
 * @see com.google.gson.stream.JsonReader
 * @see org.motechproject.mds.json.LookupWriter
 */
public class LookupReader {
    private JsonReader jsonReader;
    private Entity entity;
    private ObjectReader objectReader;

    public LookupReader(JsonReader jsonReader, Entity entity) {
        this.jsonReader = jsonReader;
        this.entity = entity;
        this.objectReader = new ObjectReader(jsonReader);
    }

    public void readLookup() throws IOException {
        Lookup lookup = new Lookup();
        lookup.setEntity(entity);

        jsonReader.beginObject();
        lookup.setLookupName(objectReader.readString("name"));
        lookup.setSingleObjectReturn(objectReader.readBoolean("singleObjectReturn"));
        lookup.setMethodName(objectReader.readString("methodName"));
        lookup.setFields(readFields());
        lookup.setRangeLookupFields(objectReader.readStringArray("rangeFields"));
        lookup.setCustomOperators(objectReader.readStringMap("customOperators"));
        lookup.setUseGenericParams(objectReader.readBooleanMap("useGenericParams"));
        lookup.setFieldsOrder(objectReader.readStringArray("fieldsOrder"));
        jsonReader.endObject();

        Lookup existingLookup = entity.getLookupByName(lookup.getLookupName());
        if (null == existingLookup) {
            entity.addLookup(lookup);
        } else if (!existingLookup.isReadOnly()) {
            entity.removeLookup(existingLookup.getId());
            entity.addLookup(lookup);
        } else {
            throw new LookupReadOnlyException("Cannot import lookup " + lookup.getLookupName());
        }
    }

    private List<Field> readFields() throws IOException {
        List<Field> fields = new ArrayList<>();
        objectReader.expect("fields");
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextString();
            Field field = entity.getField(fieldName);
            if (null != field) {
                fields.add(field);
            } else {
                throw new FieldNotFoundException(entity.getClassName(), fieldName);
            }
        }
        jsonReader.endArray();
        return fields;
    }
}
