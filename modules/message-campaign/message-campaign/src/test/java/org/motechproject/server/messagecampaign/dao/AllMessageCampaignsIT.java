package org.motechproject.server.messagecampaign.dao;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.JodaFormatter;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.loader.CampaignJsonLoader;
import org.motechproject.server.messagecampaign.userspecified.CampaignMessageRecord;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testMessageCampaignApplicationContext.xml")
public class AllMessageCampaignsIT {

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    private CampaignJsonLoader campaignJsonLoader = new CampaignJsonLoader();

    @Before
    public void setUp() {
        allMessageCampaigns.removeAll();
    }

    @After
    public void tearDown() {
        allMessageCampaigns.removeAll();
    }

    @Test
    public void getAbsoluteDatesMessageProgramTest() {
        loadCampaigns();
        String campaignName = "Absolute Dates Message Program";

        AbsoluteCampaign campaign = (AbsoluteCampaign) allMessageCampaigns.getCampaign(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.getName());
        List<AbsoluteCampaignMessage> messages = campaign.getMessages();
        assertEquals(2, messages.size());
        DateTime firstDate = new DateTime(2013, 6, 15, 0, 0, 0, 0);
        DateTime secondDate = new DateTime(2013, 6, 22, 0, 0, 0, 0);
        assertMessageWithAbsoluteSchedule(messages.get(0), "First", new String[]{"IVR", "SMS"}, "random-1", firstDate.toLocalDate());
        assertMessageWithAbsoluteSchedule(messages.get(1), "Second", new String[]{"IVR"}, "random-2", secondDate.toLocalDate());
    }

    @Test
    public void getRelativeDatesMessageProgramTest() {
        loadCampaigns();
        String campaignName = "Relative Dates Message Program";

        OffsetCampaign campaign = (OffsetCampaign) allMessageCampaigns.getCampaign(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.getName());
        List<OffsetCampaignMessage> messages = campaign.getMessages();
        assertEquals(3, messages.size());
        assertMessageWithRelativeSchedule(messages.get(0), "Week 1", new String[]{"IVR"}, "child-info-week-1", "1 Week");
        assertMessageWithRelativeSchedule(messages.get(1), "Week 1A", new String[]{"SMS"}, "child-info-week-1a", "1 Week");
        assertMessageWithRelativeSchedule(messages.get(2), "Week 1B", new String[]{"SMS"}, "child-info-week-1b", "9 Days");
    }

    @Test
    public void getCronBasedMessageProgramTest() {
        loadCampaigns();
        String campaignName = "Cron based Message Program";

        CronBasedCampaign campaign = (CronBasedCampaign) allMessageCampaigns.getCampaign(campaignName);
        assertNotNull(campaign);
        assertEquals(campaignName, campaign.getName());
        assertEquals("5 weeks", campaign.maxDuration());
        List<CronBasedCampaignMessage> messages = campaign.getMessages();
        assertEquals(1, messages.size());
        assertMessageWithCronSchedule(messages.get(0), "First", new String[]{"IVR", "SMS"}, "cron-message", "0 11 11 11 11 ?");
    }

    @Test
    public void getCampaignMessageGivenACampaignNameAndMessageKey() {
        loadCampaigns();
        String campaignName = "Relative Dates Message Program";
        String messageKey = "child-info-week-1";

        CampaignMessage campaignMessage = allMessageCampaigns.getMessage(campaignName, messageKey);
        assertNotNull(campaignMessage);
        assertEquals(campaignMessage.formats(), asList("IVR"));
        assertEquals(campaignMessage.languages(), asList("en"));
    }

    @Test
    public void getCampaignMessageGivenANonExistingCampaignNameAndMessageKey() {
        loadCampaigns();
        String campaignName = "Relative Dates Message Program";
        String messageKey = "child-info-week-1";

        CampaignMessage campaignMessage = allMessageCampaigns.getMessage("non-existing-campaign-name", messageKey);
        assertNull(campaignMessage);

        campaignMessage = allMessageCampaigns.getMessage(campaignName, "non-existing-message-key");
        assertNull(campaignMessage);
    }

    @Test
    public void shouldAddAndUpdateRecords() {
        CampaignRecord campaign = createCampaignRecord();
        CampaignRecord campaign2 = createCampaignRecord();
        CampaignRecord campaign3 = createCampaignRecord();
        campaign3.setName("OTHER NAME");

        // add first
        allMessageCampaigns.saveOrUpdate(campaign);

        assertEquals(asList(campaign), allMessageCampaigns.getAll());

        // add second
        allMessageCampaigns.saveOrUpdate(campaign3);

        assertEquals(asList(campaign, campaign3), allMessageCampaigns.getAll());

        // update first
        campaign2.setMaxDuration("20");

        allMessageCampaigns.saveOrUpdate(campaign2);

        assertEquals(asList(campaign2, campaign3), allMessageCampaigns.getAll());
    }

    @Test
    public void shouldDeleteCampaignRecords() {
        CampaignRecord campaign = createCampaignRecord();

        allMessageCampaigns.saveOrUpdate(campaign);

        assertEquals(asList(campaign), allMessageCampaigns.getAll());

        allMessageCampaigns.remove(campaign);

        assertTrue(allMessageCampaigns.getAll().isEmpty());
    }

    @Test
    public void shouldFindCampaignsByName() {
        CampaignRecord campaign = createCampaignRecord();
        CampaignRecord campaign2 = createCampaignRecord();
        campaign2.setName("Different Name");

        allMessageCampaigns.saveOrUpdate(campaign);
        allMessageCampaigns.saveOrUpdate(campaign2);

        assertEquals(asList(campaign), allMessageCampaigns.findByName("PREGNANCY"));
        assertEquals(campaign, allMessageCampaigns.findFirstByName("PREGNANCY"));
        assertEquals(asList(campaign2), allMessageCampaigns.findByName("Different Name"));
    }

    private void assertMessageWithAbsoluteSchedule(AbsoluteCampaignMessage message, String name, String[] formats, Object messageKey, LocalDate date) {
        assertMessage(message, name, formats, messageKey);
        assertEquals(date, message.date());
    }

    private void assertMessageWithRelativeSchedule(OffsetCampaignMessage message, String name, String[] formats, Object messageKey, String timeOffset) {
        assertMessage(message, name, formats, messageKey);
        assertEquals(new JodaFormatter().parsePeriod(timeOffset), message.timeOffset());
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
        assertEquals(new HashSet<String>(asList(expectedFormats)), new HashSet<String>(actualFormats));
    }

    private void loadCampaigns() {
        List<CampaignRecord> records = campaignJsonLoader.loadCampaigns("message-campaigns.json");
        for (CampaignRecord record : records) {
            allMessageCampaigns.add(record);
        }
    }

    private CampaignRecord createCampaignRecord() {
        CampaignRecord campaign = new CampaignRecord();
        campaign.setCampaignType(CampaignType.ABSOLUTE);
        campaign.setMaxDuration("10");
        campaign.setName("PREGNANCY");

        CampaignMessageRecord message = new CampaignMessageRecord();
        message.setDate(LocalDate.now());
        message.setStartTime("20:44:00");
        message.setMessageKey("key");
        message.setLanguages(asList("lang1", "lang2", "lang3"));

        campaign.setMessages(asList(message));

        return campaign;
    }
}