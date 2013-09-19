package org.motechproject.tasks.contract;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.motechproject.tasks.json.ActionEventRequestDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ChannelRequestTest {

    @Test
    public void shouldDeserializeChannelJson() throws IOException {
        String triggerEvent = "{ displayName: 'displayName', subject: 'subject', eventParameters: [{ displayName: 'displayName', eventKey: 'eventKey' }] }";
        String channel = String.format("{displayName: %s, moduleName: %s, moduleVersion: %s, triggerTaskEvents: [%s]}", "foo", "bar", "1.0", triggerEvent);
        InputStream stream = new ByteArrayInputStream(channel.getBytes(Charset.forName("UTF-8")));

        Type type = new TypeToken<ChannelRequest>() {
        }.getType();
        StringWriter writer = new StringWriter();

        IOUtils.copy(stream, writer);
        Map<Type, Object> typeAdapters = new HashMap<>();
        typeAdapters.put(ActionEventRequest.class, new ActionEventRequestDeserializer());
        ChannelRequest channelRequest = (ChannelRequest) new MotechJsonReader().readFromString(writer.toString(), type, typeAdapters);

        assertEquals(1, channelRequest.getTriggerTaskEvents().size());
    }

    @Test
    public void shouldDeserializeTriggerTaskEventJson() throws IOException {
        String triggerEvent = "[{ displayName: 'displayName', subject: 'subject', eventParameters: [{ displayName: 'displayName', eventKey: 'eventKey' }] }]";
        InputStream stream = new ByteArrayInputStream(triggerEvent.getBytes(Charset.forName("UTF-8")));

        Type type = new TypeToken<List<TriggerEventRequest>>() {
        }.getType();
        StringWriter writer = new StringWriter();

        IOUtils.copy(stream, writer);
        List<TriggerEventRequest> triggerEventRequest = (List<TriggerEventRequest>) new MotechJsonReader().readFromString(writer.toString(), type, new HashMap<Type, Object>());

        assertEquals(1, triggerEventRequest.size());
    }

}
