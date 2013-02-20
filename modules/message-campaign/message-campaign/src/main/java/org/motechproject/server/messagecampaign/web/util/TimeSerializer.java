package org.motechproject.server.messagecampaign.web.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.commons.date.model.Time;

import java.io.IOException;

public class TimeSerializer extends JsonSerializer<Time> {

    @Override
    public void serialize(Time time, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(String.format("%02d:%02d:00", time.getHour(), time.getMinute()));
    }
}
