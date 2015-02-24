package org.motechproject.mds.json;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.util.Constants;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The <code>ObjectWriter</code> class is a wrapper for JsonWriter that provides methods that performs common
 * writer tasks such as serializing arrays or formatting values.
 *
 * @see com.google.gson.stream.JsonWriter
 */
public class ObjectWriter {

    private JsonWriter jsonWriter;

    public ObjectWriter(JsonWriter jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    public void writeFormatted(String name, Object object) throws IOException {
        jsonWriter.name(name);
        writeFormatted(object);
    }

    private void writeFormatted(Object object) throws IOException {
        if (null != object) {
            if (object instanceof Boolean) {
                jsonWriter.value((Boolean) object);
            } else if (object instanceof Number) {
                jsonWriter.value((Number) object);
            } else if (object instanceof Date) {
                jsonWriter.value(Constants.Util.DEFAULT_DATE_FORMAT.format(object));
            } else {
                jsonWriter.value(String.valueOf(object));
            }
        } else {
            jsonWriter.nullValue();
        }
    }

    public void writeArray(String name, List<?> list) throws IOException {
        jsonWriter.name(name);
        jsonWriter.beginArray();
        for (Object entry : list) {
            writeFormatted(entry);
        }
        jsonWriter.endArray();
    }

    public void writerMap(String name, Map<?, ?> map) throws IOException {
        jsonWriter.name(name);
        jsonWriter.beginObject();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            jsonWriter.name(String.valueOf(entry.getKey()));
            writeFormatted(entry.getValue());
        }
        jsonWriter.endObject();
    }
}
