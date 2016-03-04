package org.motechproject.mds.testutil.records;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Serializer for {@code Record} class.
 */
public class BlobSerializer extends JsonSerializer<Byte[]> {
    @Override
    public void serialize(Byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeBinary(Base64.encodeBase64(ArrayUtils.toPrimitive(value)));
    }
}
