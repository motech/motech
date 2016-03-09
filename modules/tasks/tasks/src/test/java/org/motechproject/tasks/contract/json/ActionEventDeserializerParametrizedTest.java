package org.motechproject.tasks.contract.json;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.builder.ActionParameterRequestBuilder;
import org.motechproject.tasks.contract.builder.TestActionEventRequestBuilder;

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
public class ActionEventDeserializerParametrizedTest {
    private Map<Type, Object> typeAdapters = new HashMap<>();
    private Type type = new TypeToken<ChannelRequest>() {
    }.getType();
    private MotechJsonReader reader = new MotechJsonReader();
    private String channelAsString;
    private List<ActionEventRequest> expected;

    public ActionEventDeserializerParametrizedTest(String path, List<ActionEventRequest> events) throws IOException {
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

        parameters.add(new ActionParameterRequestBuilder().setKey("DosageID")
                .setDisplayName("pillreminder.dossageID").setOrder(0).createActionParameterRequest());

        parameters.add(new ActionParameterRequestBuilder().setKey("ExternalID")
                .setDisplayName("pillreminder.externalID").setOrder(1).createActionParameterRequest());

        parameters.add(new ActionParameterRequestBuilder().setKey("times-reminders-sent")
                .setDisplayName("pillreminder.times.sent").setOrder(2).setType("INTEGER").createActionParameterRequest());

        parameters.add(new ActionParameterRequestBuilder().setKey("times-reminders-to-be-sent")
                .setDisplayName("pillreminder.total.times.sent").setOrder(3).setType("INTEGER").createActionParameterRequest());

        parameters.add(new ActionParameterRequestBuilder().setKey("retry-interval")
                .setDisplayName("pillreminder.retry.interval").setOrder(4).setType("INTEGER").createActionParameterRequest());

        ActionEventRequest event = new TestActionEventRequestBuilder().setDisplayName("pillreminder.event.subject.scheduler")
                .setSubject("org.motechproject.server.pillreminder.scheduler-reminder").setDescription("description")
                .setServiceInterface(null).setServiceMethod(null).setActionParameters(parameters).createActionEventRequest();

        List<ActionEventRequest> events = new ArrayList<>();
        events.add(event);
        return events;
    }

    private static List<ActionEventRequest> getMessageCampaignEvents() {
        SortedSet<ActionParameterRequest> parameters1 = new TreeSet<>();

        parameters1.add(new ActionParameterRequestBuilder().setKey("CampaignName")
                .setDisplayName("msgCampaign.campaign.name").setOrder(0).createActionParameterRequest());

        parameters1.add(new ActionParameterRequestBuilder().setKey("ExternalID")
                .setDisplayName("msgCampaign.externalID").setOrder(1).createActionParameterRequest());

        parameters1.add(new ActionParameterRequestBuilder().setKey("MessageKey")
                .setDisplayName("msgCampaign.message.key").setOrder(2).createActionParameterRequest());

        ActionEventRequest event1 = new TestActionEventRequestBuilder().setDisplayName("msgCampaign.send.message")
                .setSubject("org.motechproject.messagecampaign.fired-campaign-message").setDescription("description")
                .setServiceInterface(null).setServiceMethod(null).setActionParameters(parameters1).createActionEventRequest();


        SortedSet<ActionParameterRequest> parameters2 = new TreeSet<>();

        parameters2.add(new ActionParameterRequestBuilder().setKey("ExternalID")
                .setDisplayName("msgCampaign.externalID").setOrder(0).createActionParameterRequest());

        parameters2.add(new ActionParameterRequestBuilder().setKey("CampaignName")
                .setDisplayName("msgCampaign.campaign.name").setOrder(1).createActionParameterRequest());

        ActionEventRequest event2 = new TestActionEventRequestBuilder().setDisplayName("msgCampaign.campaign.completed")
                .setSubject("org.motechproject.messagecampaign.campaign-completed").setDescription("description")
                .setServiceInterface("org.motechproject.messagecampaign.service.MessageCampaignService")
                .setServiceMethod("campaignCompleted").setActionParameters(parameters2).createActionEventRequest();

        return asList(event1, event2);
    }
}
