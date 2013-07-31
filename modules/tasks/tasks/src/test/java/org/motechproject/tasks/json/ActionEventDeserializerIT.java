package org.motechproject.tasks.json;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.service.ActionEventRequest;
import org.motechproject.tasks.service.ActionParameterRequest;
import org.motechproject.tasks.service.ChannelRequest;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ActionEventDeserializerIT {
    private Map<Type, Object> typeAdapters = new HashMap<>();
    private Type type = new TypeToken<ChannelRequest>() {
    }.getType();
    private MotechJsonReader reader = new MotechJsonReader();
    private String channelAsString;
    private List<ActionEventRequest> expected;

    public ActionEventDeserializerIT(String path, List<ActionEventRequest> events) throws IOException {
        typeAdapters.put(ActionEventRequest.class, new ActionEventRequestDeserializer());

        StringWriter writer = new StringWriter();
        IOUtils.copy(this.getClass().getResourceAsStream(path), writer);

        channelAsString = writer.toString();
        expected = events;
    }

    @Parameters
    public static Collection<Object[]> testParameters() {
        return asList(new Object[][]{
                {"/pillreminder-test-channel.json", getPillReminderEvents()},
                {"/message-campaign-test-channel.json", getMessageCampaignEvents()}
        });
    }

    @Test
    public void shouldDeserializeJson() {
        ChannelRequest channelRequest = (ChannelRequest) reader.readFromString(channelAsString, type, typeAdapters);

        List<ActionEventRequest> actionTaskEvents = channelRequest.getActionTaskEvents();
        assertEquals(expected, actionTaskEvents);
    }

    private static List<ActionEventRequest> getPillReminderEvents() {
        SortedSet<ActionParameterRequest> parameters = new TreeSet<>();
        parameters.add(new ActionParameterRequest("DosageID", "pillreminder.dossageID", 0));
        parameters.add(new ActionParameterRequest("ExternalID", "pillreminder.externalID", 1));
        parameters.add(new ActionParameterRequest("times-reminders-sent", "pillreminder.times.sent", 2, "INTEGER"));
        parameters.add(new ActionParameterRequest("times-reminders-to-be-sent", "pillreminder.total.times.sent", 3, "INTEGER"));
        parameters.add(new ActionParameterRequest("retry-interval", "pillreminder.retry.interval", 4, "INTEGER"));

        ActionEventRequest event = new ActionEventRequest("pillreminder.event.subject.scheduler", "org.motechproject.server.pillreminder.scheduler-reminder", "description", null, null, parameters);

        List<ActionEventRequest> events = new ArrayList<>();
        events.add(event);
        return events;
    }

    private static List<ActionEventRequest> getMessageCampaignEvents() {
        SortedSet<ActionParameterRequest> parameters1 = new TreeSet<>();
        parameters1.add(new ActionParameterRequest("CampaignName", "msgCampaign.campaign.name", 0));
        parameters1.add(new ActionParameterRequest("ExternalID", "msgCampaign.externalID", 1));
        parameters1.add(new ActionParameterRequest("MessageKey", "msgCampaign.message.key", 2));

        ActionEventRequest event1 = new ActionEventRequest("msgCampaign.send.message", "org.motechproject.messagecampaign.fired-campaign-message", "description", null, null, parameters1);


        SortedSet<ActionParameterRequest> parameters2 = new TreeSet<>();
        parameters2.add(new ActionParameterRequest("ExternalID", "msgCampaign.externalID", 0));
        parameters2.add(new ActionParameterRequest("CampaignName", "msgCampaign.campaign.name", 1));

        ActionEventRequest event2 = new ActionEventRequest("msgCampaign.campaign.completed", "org.motechproject.messagecampaign.campaign-completed", "description", "org.motechproject.messagecampaign.service.MessageCampaignService", "campaignCompleted", parameters2);

        return asList(event1, event2);
    }
}
