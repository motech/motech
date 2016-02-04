package org.motechproject.scheduler.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.DateTime;

import java.io.IOException;

public class CustomDateDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            return CustomDateParser.parseToDateTime(jp.getText());
        } catch (Exception e) {
            throw new JsonMappingException(String.format("Cannot parse date %s", jp.getText()), e);
        }
    }
}
