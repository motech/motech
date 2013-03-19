package org.motechproject.tasks.json;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.ParameterType;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
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
    private Type type = new TypeToken<Channel>() {}.getType();
    private MotechJsonReader reader = new MotechJsonReader();
    private String channelAsString;
    private List<ActionEvent> expected;

    public ActionEventDeserializerIT(String path, List<ActionEvent> events) throws IOException {
        typeAdapters.put(ActionEvent.class, new ActionEventDeserializer());

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
        Channel channel = (Channel) reader.readFromString(channelAsString, type, typeAdapters);

        assertEquals(expected, channel.getActionTaskEvents());
    }

    private static List<ActionEvent> getPillReminderEvents() {
        ActionEvent event = new ActionEvent();
        event.setDisplayName("pillreminder.event.subject.scheduler");
        event.setSubject("org.motechproject.server.pillreminder.scheduler-reminder");

        SortedSet<ActionParameter> parameters = new TreeSet<>();
        parameters.add(new ActionParameter("pillreminder.dossageID", "DosageID", 0));
        parameters.add(new ActionParameter("pillreminder.externalID", "ExternalID", 1));
        parameters.add(new ActionParameter("pillreminder.times.sent", "times-reminders-sent", ParameterType.INTEGER, 2));
        parameters.add(new ActionParameter("pillreminder.total.times.sent", "times-reminders-to-be-sent", ParameterType.INTEGER, 3));
        parameters.add(new ActionParameter("pillreminder.retry.interval", "retry-interval", ParameterType.INTEGER, 4));

        event.setActionParameters(parameters);

        return asList(event);
    }

    private static List<ActionEvent> getMessageCampaignEvents() {
        ActionEvent event1 = new ActionEvent();
        event1.setDisplayName("messagecampaign.send.message");
        event1.setSubject("org.motechproject.messagecampaign.fired-campaign-message");

        SortedSet<ActionParameter> parameters1 = new TreeSet<>();
        parameters1.add(new ActionParameter("messagecampaign.campaign.name", "CampaignName", 0));
        parameters1.add(new ActionParameter("messagecampaign.externalID", "ExternalID", 1));
        parameters1.add(new ActionParameter("messagecampaign.message.key", "MessageKey", 2));

        event1.setActionParameters(parameters1);

        ActionEvent event2 = new ActionEvent();
        event2.setDisplayName("messagecampaign.campaign.completed");
        event2.setServiceInterface("org.motechproject.messagecampaign.service.MessageCampaignService");
        event2.setServiceMethod("campaignCompleted");
        event2.setSubject("org.motechproject.messagecampaign.campaign-completed");

        SortedSet<ActionParameter> parameters2 = new TreeSet<>();
        parameters2.add(new ActionParameter("messagecampaign.externalID", "ExternalID", 0));
        parameters2.add(new ActionParameter("messagecampaign.campaign.name", "CampaignName", 1));

        event2.setActionParameters(parameters2);

        return asList(event1, event2);
    }
}
