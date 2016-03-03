package org.motechproject.mds.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

/**
 * Class responsible for deserializing blob from Base64 to Byte[].
 */
public class BlobDeserializer extends JsonDeserializer<Byte[]> {

    @Override
    public Byte[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return ArrayUtils.toObject(Base64.decodeBase64(jp.getBinaryValue()));
    }
}
