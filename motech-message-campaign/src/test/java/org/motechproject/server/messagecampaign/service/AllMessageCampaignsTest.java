package org.motechproject.server.messagecampaign.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.*;

import java.util.*;

import static junit.framework.Assert.*;

public class AllMessageCampaignsTest {

    private AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.setProperty("messagecampaign.definition.file", "/simple-message-campaign.json");
        MotechJsonReader motechJsonReader = new MotechJsonReader();
        allMessageCampaigns = new AllMessageCampaigns(properties, motechJsonReader);
    }

    @Test
    public void getAbsoluteDatesMessageProgramTest() {
        String campaignName = "Absolute Dates Message Program";

        AbsoluteCampaign campaign = (AbsoluteCampaign) allMessageCampaigns.get(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.name());
        List<AbsoluteCampaignMessage> messages = campaign.messages();
        assertEquals(2, messages.size());
        DateTime firstDate = new DateTime(2011, 6, 15, 0, 0, 0, 0);
        DateTime secondDate = new DateTime(2011, 6, 22, 0, 0, 0, 0);
        assertMessageWithAbsoluteSchedule(messages.get(0), "First", new String[]{"IVR", "SMS"}, "random-1", firstDate.toDate());
        assertMessageWithAbsoluteSchedule(messages.get(1), "Second", new String[]{"IVR"}, "random-2", secondDate.toDate());
    }

    @Test
    public void getRelativeDatesMessageProgramTest() {
        String campaignName = "Relative Dates Message Program";

        OffsetCampaign campaign = (OffsetCampaign) allMessageCampaigns.get(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.name());
        List<OffsetCampaignMessage> messages = campaign.messages();
        assertEquals(3, messages.size());
        assertMessageWithRelativeSchedule(messages.get(0), "Week 1", new String[]{"IVR"}, "child-info-week-1", "1 Week");
        assertMessageWithRelativeSchedule(messages.get(1), "Week 1A", new String[]{"SMS"}, "child-info-week-1a", "1 Week");
        assertMessageWithRelativeSchedule(messages.get(2), "Week 1B", new String[]{"SMS"}, "child-info-week-1b", "9 Days");
    }

    @Test
    public void getRelativeParameterizedDatesMessageProgramTest() {
        String campaignName = "Relative Parameterized Dates Message Program";

        RepeatingCampaign campaign = (RepeatingCampaign) allMessageCampaigns.get(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.name());
        assertEquals("5 weeks", campaign.maxDuration());
        List<RepeatingCampaignMessage> messages = campaign.messages();
        assertEquals(3, messages.size());
        assertMessageWithParameterizedRelativeSchedule(messages.get(0), "Weekly Message #1", new String[]{"IVR", "SMS"}, "child-info-week-{Offset}-1", "1 Week");
        assertMessageWithParameterizedRelativeSchedule(messages.get(1), "Weekly Message #2", new String[]{"SMS"}, "child-info-week-{Offset}-2", "9 Days");
        assertMessageWithParameterizedRelativeSchedule(messages.get(2), "Weekly Message #3", new String[]{"SMS"}, "child-info-week-{Offset}-3", "12 Days");
    }

    @Test
    public void getCronBasedMessageProgramTest() {
        String campaignName = "Cron based Message Program";

        CronBasedCampaign campaign = (CronBasedCampaign) allMessageCampaigns.get(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.name());
        List<CronBasedCampaignMessage> messages = campaign.messages();
        assertEquals(1, messages.size());
        assertMessageWithCronSchedule(messages.get(0), "First", new String[]{"IVR", "SMS"}, "cron-message", "0 11 11 11 11 ?");
    }

    @Test
    public void getCampaignMessageGivenACampaignNameAndMessageKey(){
        String campaignName = "Relative Dates Message Program";
        String messageKey = "child-info-week-1";

        CampaignMessage campaignMessage = allMessageCampaigns.get(campaignName, messageKey);
        assertNotNull(campaignMessage);
        assertEquals(campaignMessage.formats(), Arrays.asList("IVR"));
        assertEquals(campaignMessage.languages(), Arrays.asList("en"));
    }

    @Test
    public void getCampaignMessageGivenANonExistingCampaignNameAndMessageKey(){
        String campaignName = "Relative Dates Message Program";
        String messageKey = "child-info-week-1";

        CampaignMessage campaignMessage = allMessageCampaigns.get("non-existing-campaign-name", messageKey);
        assertNull(campaignMessage);

        campaignMessage = allMessageCampaigns.get(campaignName, "non-existing-message-key");
        assertNull(campaignMessage);
    }

    private void assertMessageWithAbsoluteSchedule(AbsoluteCampaignMessage message, String name, String[] formats, Object messageKey, Date date) {
        assertMessage(message, name, formats, messageKey);
        assertEquals(date, message.date().toDate());
    }

    private void assertMessageWithRelativeSchedule(OffsetCampaignMessage message, String name, String[] formats, Object messageKey, String timeOffset) {
        assertMessage(message, name, formats, messageKey);
        assertEquals(timeOffset, message.timeOffset());
    }

    private void assertMessageWithParameterizedRelativeSchedule(RepeatingCampaignMessage message, String name, String[] formats, Object messageKey, String repeatInterval) {
        assertMessage(message, name, formats, messageKey);
        assertEquals(repeatInterval, message.repeatInterval());
    }

    private void assertMessageWithCronSchedule(CronBasedCampaignMessage message, String name, String[] formats, Object messageKey, String cron) {
        assertMessage(message, name, formats, messageKey);
        assertEquals(cron, message.cron());
    }

    private void assertMessage(CampaignMessage message, String name, String[] formats, Object messageKey) {
        assertEquals(name, message.name());
        assertCollection(formats, message.formats());
        assertCollection(new String[]{"en"}, message.languages());
        assertEquals(messageKey, message.messageKey());
    }

    private void assertCollection(String[] expectedFormats, List<String> actualFormats) {
        assertEquals(new HashSet<String>(Arrays.asList(expectedFormats)), new HashSet<String>(actualFormats));
    }
}