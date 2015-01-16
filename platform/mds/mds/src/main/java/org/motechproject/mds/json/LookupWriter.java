package org.motechproject.mds.json;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.domain.Lookup;

import java.io.IOException;
import java.util.List;

/**
 * The <code>LookupWriter</code> class is a wrapper for JsonWriter that provides ability to serialize lookup definition
 * to underlying writer.
 * <p>
 * Generated json format:
 * <p><pre>{@code
 *   {
 *     "name": {{lookup name}}
 *     "singleObjectReturn": {{true if lookup returns single value, false otherwise}}
 *     "methodName": {{lookup method name}}
 *     "fields": [ {{lookup fields names}} ]
 *     "rangeFields": [ {{lookup range fields names}} ]
 *     "customOperators": { {{custom operators map; keys: fields names, values: custom operators}} }
 *     "useGenericParams": { {{generic params discriminator; keys: fields names, values: true or false}} }
 *   }
 * }</pre>
 *
 * @see org.motechproject.mds.domain.Lookup
 * @see org.motechproject.mds.json.LookupWriter
 * @see org.motechproject.mds.json.EntityWriter
 * @see com.google.gson.stream.JsonWriter
 */
public class LookupWriter {

    private JsonWriter jsonWriter;

    public LookupWriter(JsonWriter jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    public void writeLookup(Lookup lookup) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(lookup.getLookupName());
        jsonWriter.name("singleObjectReturn").value(lookup.isSingleObjectReturn());
        jsonWriter.name("methodName").value(lookup.getMethodName());

        FieldWriter fieldWriter = new FieldWriter(jsonWriter);

        fieldWriter.writeFieldNamesArray("fields", lookup.getFields());

        ObjectWriter objectWriter = new ObjectWriter(jsonWriter);

        objectWriter.writeArray("rangeFields", lookup.getRangeLookupFields());
        objectWriter.writerMap("customOperators", lookup.getCustomOperators());
        objectWriter.writerMap("useGenericParams", lookup.getUseGenericParams());

        jsonWriter.endObject();
    }

    public void writeLookupNamesArray(String name, List<Lookup> lookups) throws IOException {
        jsonWriter.name(name);
        jsonWriter.beginArray();
        for (Lookup lookup : lookups) {
            jsonWriter.value(lookup.getLookupName());
        }
        jsonWriter.endArray();
    }
}
