package org.motechproject.mds.config;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.motechproject.mds.util.Constants.Config.MDS_DELETE_MODE;
import static org.motechproject.mds.util.Constants.Config.MDS_EMPTY_TRASH;
import static org.motechproject.mds.util.Constants.Config.MDS_TIME_UNIT;
import static org.motechproject.mds.util.Constants.Config.MDS_TIME_VALUE;

/**
 * The <code>ModuleSettings</code> contains the base module settings which are inside the
 * {@link org.motechproject.mds.util.Constants.Config#MODULE_FILE}. The getters and setters
 * inside this class always checks the given property and if it is incorrect then the default
 * value of the given property will be returned.
 */
@JsonSerialize(using = ModuleSettings.Serializer.class)
@JsonDeserialize(using = ModuleSettings.Deserializer.class)
public class ModuleSettings extends Properties {
    private static final long serialVersionUID = -3738155444078581700L;

    public static final DeleteMode DEFAULT_DELETE_MODE = DeleteMode.TRASH;
    public static final Boolean DEFAULT_EMPTY_TRASH = false;
    public static final Integer DEFAULT_TIME_VALUE = 1;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    ModuleSettings() {
        // prevent to create separate module settings
    }

    public DeleteMode getDeleteMode() {
        String valueAsString = getProperty(MDS_DELETE_MODE);
        DeleteMode value = DEFAULT_DELETE_MODE;

        if (isNotBlank(valueAsString)) {
            value = DeleteMode.fromString(valueAsString);
        }

        return value;
    }

    public void setDeleteMode(String deleteMode) {
        DeleteMode fromString = DeleteMode.fromString(deleteMode);

        if (DeleteMode.UNKNOWN == fromString) {
            setDeleteMode(DEFAULT_DELETE_MODE);
        } else {
            setDeleteMode(fromString);
        }
    }

    public void setDeleteMode(DeleteMode deleteMode) {
        DeleteMode mode = DeleteMode.UNKNOWN == deleteMode ? DEFAULT_DELETE_MODE : deleteMode;
        set(MDS_DELETE_MODE, mode, DEFAULT_DELETE_MODE);
    }

    public Boolean isEmptyTrash() {
        String valueAsString = getProperty(MDS_EMPTY_TRASH);
        Boolean value = DEFAULT_EMPTY_TRASH;

        if (isNotBlank(valueAsString)) {
            value = Boolean.parseBoolean(valueAsString);
        }

        return value;
    }

    public void setEmptyTrash(String emptyTrash) {
        if (isNotBlank(emptyTrash)) {
            setEmptyTrash(Boolean.parseBoolean(emptyTrash));
        } else {
            setEmptyTrash(DEFAULT_EMPTY_TRASH);
        }
    }

    public void setEmptyTrash(Boolean emptyTrash) {
        set(MDS_EMPTY_TRASH, emptyTrash, DEFAULT_EMPTY_TRASH);
    }

    public Integer getTimeValue() {
        String valueAsString = getProperty(MDS_TIME_VALUE);
        Integer value = DEFAULT_TIME_VALUE;

        if (isNotBlank(valueAsString) && isNumeric(valueAsString)) {
            value = Integer.parseInt(valueAsString);
        }

        if (value < 1) {
            value = DEFAULT_TIME_VALUE;
        }

        return value;
    }

    public void setTimeValue(String timeValue) {
        if (isNotBlank(timeValue) && isNumeric(timeValue)) {
            setTimeValue(Integer.parseInt(timeValue));
        } else {
            setTimeValue(DEFAULT_TIME_VALUE);
        }
    }

    public void setTimeValue(Integer timeValue) {
        Integer value = timeValue < 1 ? DEFAULT_TIME_VALUE : timeValue;
        set(MDS_TIME_VALUE, value, DEFAULT_TIME_VALUE);
    }

    public TimeUnit getTimeUnit() {
        String valueAsString = getProperty(MDS_TIME_UNIT);
        TimeUnit value = DEFAULT_TIME_UNIT;

        if (isNotBlank(valueAsString)) {
            value = TimeUnit.fromString(valueAsString);
        }

        return value;
    }

    public void setTimeUnit(String timeUnit) {
        TimeUnit fromString = TimeUnit.fromString(timeUnit);

        if (TimeUnit.UNKNOWN == fromString) {
            setTimeUnit(DEFAULT_TIME_UNIT);
        } else {
            setTimeUnit(fromString);
        }
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        TimeUnit unit = TimeUnit.UNKNOWN == timeUnit ? DEFAULT_TIME_UNIT : timeUnit;
        set(MDS_TIME_UNIT, unit, DEFAULT_TIME_UNIT);
    }

    private void set(String key, Object given, Object defaultValue) {
        Object value = null == given ? defaultValue : given;
        String valueAsString = value.toString();

        setProperty(key, valueAsString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeleteMode(), isEmptyTrash(), getTimeValue(), getTimeUnit());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ModuleSettings other = (ModuleSettings) obj;

        return Objects.equals(this.getDeleteMode(), other.getDeleteMode())
                && Objects.equals(this.isEmptyTrash(), other.isEmptyTrash())
                && Objects.equals(this.getTimeValue(), other.getTimeValue())
                && Objects.equals(this.getTimeUnit(), other.getTimeUnit());
    }

    @Override
    public String toString() {
        return String.format(
                "ModuleSettings{deleteMode=%s, emptyTrash=%s, timeValue=%d, timeUnit=%s}",
                getDeleteMode(), isEmptyTrash(), getTimeValue(), getTimeUnit()
        );
    }

    public static final class Serializer extends JsonSerializer<ModuleSettings> {

        @Override
        public void serialize(ModuleSettings value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeObjectField("deleteMode", value.getDeleteMode());
            jgen.writeObjectField("emptyTrash", value.isEmptyTrash());
            jgen.writeObjectField("timeValue", value.getTimeValue());
            jgen.writeObjectField("timeUnit", value.getTimeUnit());
            jgen.writeEndObject();
        }
    }

    public static final class Deserializer extends JsonDeserializer<ModuleSettings> {

        @Override
        public ModuleSettings deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode jsonNode = jp.readValueAsTree();

            String deleteMode = getValue(jsonNode, "deleteMode");
            String emptyTrash = getValue(jsonNode, "emptyTrash");
            String timeValue = getValue(jsonNode, "timeValue");
            String timeUnit = getValue(jsonNode, "timeUnit");

            ModuleSettings settings = new ModuleSettings();
            settings.setDeleteMode(deleteMode);
            settings.setEmptyTrash(emptyTrash);
            settings.setTimeValue(timeValue);
            settings.setTimeUnit(timeUnit);

            return settings;
        }

        private String getValue(JsonNode jsonNode, String key) {
            String value = null;

            if (jsonNode.has(key)) {
                value = jsonNode.get(key).asText();
            }

            return value;
        }

    }
}
