package org.motechproject.mds.json;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.FieldValidation;

import java.io.IOException;
import java.util.List;

/**
 * The <code>FieldWriter</code> class is a wrapper for JsonWriter that provides ability to serialize field definition
 * to underlying writer.
 * <p>
 * Generated json format:
 * <p><pre>{@code
 *   {
 *     "name": {{field name}},
 *     "displayName": {{field display name}},
 *     "required": {{true if field is required, false otherwise}},
 *     "defaultValue": {{field default value}},
 *     "tooltip": {{field tooltip}},
 *     "placeholder": {{field placeholder}},
 *     "type": {{field type symbolic name}},
 *     "metadata": [ {{field metadata formatted as:}}
 *       {
 *         "key": {{metadata entry key}},
 *         "value": {{metadata entry value}}
 *       }
 *     ],
 *     "validations": [ {{field validations formatted as:}}
 *       {
 *         "key": {{validation symbolic name}},
 *         "value": {{validation value}},
 *         "enabled": {{true if validation is enabled, false otherwise}}
 *       }
 *     ],
 *     "settings": [ {{field settings formatted as:}}
 *       {
 *         "key": {{setting symbolic name}},
 *         "value": {{setting value}}
 *       }
 *     ],
 *   }
 * }</pre>
 *
 * @see org.motechproject.mds.domain.Field
 * @see org.motechproject.mds.domain.FieldMetadata
 * @see org.motechproject.mds.domain.FieldValidation
 * @see org.motechproject.mds.domain.FieldSetting
 * @see org.motechproject.mds.json.EntityWriter
 * @see com.google.gson.stream.JsonWriter
 * @see org.motechproject.mds.json.FieldReader
 */
public class FieldWriter {

    private JsonWriter jsonWriter;

    public FieldWriter(JsonWriter jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    public void writeField(Field field) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(field.getName());
        jsonWriter.name("displayName").value(field.getDisplayName());
        jsonWriter.name("required").value(field.isRequired());
        jsonWriter.name("defaultValue").value(field.getDefaultValue());
        jsonWriter.name("tooltip").value(field.getTooltip());
        jsonWriter.name("placeholder").value(field.getPlaceholder());
        jsonWriter.name("type").value(field.getType().getDisplayName());

        writeMetadata(field.getMetadata());
        writeValidations(field.getValidations());
        writeSettings(field.getSettings());

        jsonWriter.endObject();
    }

    private void writeSettings(List<FieldSetting> settings) throws IOException {
        jsonWriter.name("settings");
        jsonWriter.beginArray();
        for(FieldSetting setting : settings) {
            jsonWriter.beginObject();
            jsonWriter.name("key").value(setting.getKey());
            jsonWriter.name("value").value(setting.getValue());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
    }

    private void writeValidations(List<FieldValidation> validations) throws IOException {
        jsonWriter.name("validations");
        jsonWriter.beginArray();
        for(FieldValidation validation : validations) {
            jsonWriter.beginObject();
            jsonWriter.name("key").value(validation.getDetails().getDisplayName());
            jsonWriter.name("value").value(validation.getValue());
            jsonWriter.name("enabled").value(validation.isEnabled());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
    }

    private void writeMetadata(List<FieldMetadata> metadata) throws IOException {
        jsonWriter.name("metadata");
        jsonWriter.beginArray();
        for(FieldMetadata metadataEntry : metadata) {
            jsonWriter.beginObject();
            jsonWriter.name("key").value(metadataEntry.getKey());
            jsonWriter.name("value").value(metadataEntry.getValue());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
    }

    public void writeFieldNamesArray(String name, List<Field> fields) throws IOException {
        jsonWriter.name(name);
        jsonWriter.beginArray();
        for (Field field : fields) {
            jsonWriter.value(field.getName());
        }
        jsonWriter.endArray();
    }
}
