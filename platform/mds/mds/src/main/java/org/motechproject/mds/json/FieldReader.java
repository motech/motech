package org.motechproject.mds.json;

import com.google.gson.stream.JsonReader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.FieldValidation;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.exception.field.FieldReadOnlyException;
import org.motechproject.mds.exception.type.TypeSettingNotFoundException;
import org.motechproject.mds.exception.type.TypeValidationNotFoundException;

import java.io.IOException;

/**
 * The <code>FieldReader</code> class is a wrapper for JsonReader that provides ability to deserialize field definition
 * from underlying reader that was previously written by FieldWriter.
 *
 * @see org.motechproject.mds.domain.Field
 * @see org.motechproject.mds.domain.FieldMetadata
 * @see org.motechproject.mds.domain.FieldValidation
 * @see org.motechproject.mds.domain.FieldSetting
 * @see org.motechproject.mds.json.EntityReader
 * @see com.google.gson.stream.JsonReader
 * @see org.motechproject.mds.json.FieldWriter
 */
public class FieldReader {
    private JsonReader jsonReader;
    private Entity entity;
    private ImportContext importContext;
    private ObjectReader objectReader;

    public FieldReader(JsonReader jsonReader, Entity entity, ImportContext importContext) {
        this.jsonReader = jsonReader;
        this.entity = entity;
        this.importContext = importContext;
        this.objectReader = new ObjectReader(jsonReader);
    }

    public void readField() throws IOException {
        Field field = new Field();
        field.setEntity(entity);

        jsonReader.beginObject();
        field.setName(objectReader.readString("name"));
        field.setDisplayName(objectReader.readString("displayName"));
        field.setRequired(objectReader.readBoolean("required"));
        field.setUnique(objectReader.readBoolean("unique"));
        field.setDefaultValue(objectReader.readString("defaultValue"));
        field.setTooltip(objectReader.readString("tooltip"));
        field.setPlaceholder(objectReader.readString("placeholder"));
        field.setNonEditable(objectReader.readBoolean("nonEditable"));
        field.setNonDisplayable(objectReader.readBoolean("nonDisplayable"));
        field.setType(importContext.getType(objectReader.readString("type")));
        readMetadata(field);
        readValidations(field);
        readSettings(field);
        jsonReader.endObject();

        Field existingField = entity.getField(field.getName());
        if (null == existingField || !existingField.isReadOnly()) {
            entity.addField(field);
        } else {
            throw new FieldReadOnlyException(entity.getName(), existingField.getName());
        }
    }

    private void readMetadata(Field field) throws IOException {
        objectReader.expect("metadata");
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            FieldMetadata metadata = new FieldMetadata();
            metadata.setField(field);
            jsonReader.beginObject();
            metadata.setKey(objectReader.readString("key"));
            metadata.setValue(objectReader.readString("value"));
            jsonReader.endObject();
        }
        jsonReader.endArray();
    }

    private void readSettings(Field field) throws IOException {
        objectReader.expect("settings");
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            FieldSetting setting = new FieldSetting();
            setting.setField(field);
            jsonReader.beginObject();
            setting.setDetails(getTypeSetting(field.getType(), objectReader.readString("key")));
            setting.setValue(objectReader.readString("value"));
            jsonReader.endObject();
            field.addSetting(setting);
        }
        jsonReader.endArray();
    }

    private TypeSetting getTypeSetting(Type type, String key) {
        for (TypeSetting setting : type.getSettings()) {
            if (key.equals(setting.getName())) {
                return setting;
            }
        }
        throw new TypeSettingNotFoundException("Cannot find type setting " + key + " for " + type.getDisplayName());
    }

    private void readValidations(Field field) throws IOException {
        objectReader.expect("validations");
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            FieldValidation validation = new FieldValidation();
            validation.setField(field);
            jsonReader.beginObject();
            validation.setDetails(getTypeValidation(field.getType(), objectReader.readString("key")));
            validation.setValue(objectReader.readString("value"));
            validation.setEnabled(objectReader.readBoolean("enabled"));
            jsonReader.endObject();
            field.addValidation(validation);
        }
        jsonReader.endArray();
    }

    private TypeValidation getTypeValidation(Type type, String key) {
        for (TypeValidation validation : type.getValidations()) {
            if (key.equals(validation.getDisplayName())) {
                return validation;
            }
        }
        throw new TypeValidationNotFoundException("Cannot find type validation " + key + " for " + type.getDisplayName());
    }
}
