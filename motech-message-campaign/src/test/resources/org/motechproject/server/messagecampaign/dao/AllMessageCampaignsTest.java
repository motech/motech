package org.motechproject.server.messagecampaign.dao;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AllMessageCampaignsTest {

    private Properties properties;
    private AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setup() {
        properties = new Properties();
        properties.setProperty("messagecampaign.definition.file", "simple-message-campaign.json");
        allMessageCampaigns = new AllMessageCampaigns(properties);
    }

    @Test
    public void testGet() {
        Campaign campaign = allMessageCampaigns.get("Weekly Info Child Program");
        assertNotNull(campaign);
        assertEquals("Weekly Info Child Program", campaign.getName());
        List<CampaignMessage> messages = campaign.getMessages();
        assertEquals(2, messages.size());
        assertMessage(messages.get(0), "Week 1", new String[]{"IVR"}, "child-info-week-1");
        assertMessage(messages.get(1), "Week 1A", new String[]{"SMS"}, "child-info-week-1a");
    }

    private void assertMessage(CampaignMessage message, String name, String[] formats, Object messageKey) {
        assertEquals(name, message.getName());
        assertCollection(formats, message.getFormats());
        assertCollection(new String[]{"en"}, message.getLanguages());
        assertEquals(messageKey, message.getMessageKey());
        assertEquals("1 Week", message.getTimeOffset());
    }

    private void assertCollection(String[] expectedFormats, List<String> actualFormats) {
        assertEquals(new HashSet(Arrays.asList(expectedFormats)), new HashSet(actualFormats));
    }
}
