package org.motechproject.messagecampaign.userspecified;

import org.junit.Test;
import org.motechproject.messagecampaign.builder.CampaignMessageRecordTestBuilder;
import org.motechproject.messagecampaign.builder.CampaignRecordBuilder;
import org.motechproject.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.messagecampaign.domain.campaign.Campaign;
import org.motechproject.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.commons.date.util.JodaFormatter;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CampaignRecordTest {

    private CampaignRecord campaignRecord;
    private CampaignMessageRecord messageRecord;

    @Test
    public void testBuildAbsoluteCampaign() {
        messageRecord = CampaignMessageRecordTestBuilder.createAbsoluteCampaignMessageRecord("Message 1", "message-key");
        campaignRecord = CampaignRecordBuilder.absoluteCampaignRecord("Campaign 1", messageRecord);

        Campaign campaign =  campaignRecord.build();
        assertTrue(campaign instanceof AbsoluteCampaign);
        AbsoluteCampaign absoluteCampaign = (AbsoluteCampaign) campaign;
        assertEquals(campaignRecord.getName(), absoluteCampaign.getName());
        List<AbsoluteCampaignMessage> messages = absoluteCampaign.getMessages();
        assertEquals(1, messages.size());

        AbsoluteCampaignMessage message = messages.get(0);
        assertEquals(messageRecord.getName(), message.name());
        assertEquals(messageRecord.getFormats(), message.formats());
        assertEquals(messageRecord.getLanguages(), message.languages());
        assertEquals(messageRecord.getMessageKey(), message.messageKey());
        assertEquals(messageRecord.getDate(), message.date());
    }

    @Test
    public void testBuildOffsetCampaign() {
        messageRecord = CampaignMessageRecordTestBuilder.createOffsetCampaignMessageRecord("Message 1", "message-key");
        campaignRecord = CampaignRecordBuilder.offsetCampaignRecord("Campaign 1", messageRecord);

        Campaign campaign = campaignRecord.build();
        assertTrue(campaign instanceof OffsetCampaign);
        OffsetCampaign offsetCampaign = (OffsetCampaign) campaign;
        assertEquals(campaignRecord.getName(), offsetCampaign.getName());
        assertEquals(campaignRecord.getMaxDuration(), offsetCampaign.maxDuration());
        List<OffsetCampaignMessage> messages = offsetCampaign.getMessages();
        assertEquals(1, messages.size());

        OffsetCampaignMessage message = messages.get(0);
        assertEquals(messageRecord.getName(), message.name());
        assertEquals(messageRecord.getFormats(), message.formats());
        assertEquals(messageRecord.getLanguages(), message.languages());
        assertEquals(messageRecord.getMessageKey(), message.messageKey());
        assertEquals(new JodaFormatter().parsePeriod(messageRecord.getTimeOffset()), message.timeOffset());

    }

    @Test
    public void testBuildCronBasedCampaign() {
        messageRecord = CampaignMessageRecordTestBuilder.createCronBasedCampaignMessageRecord("Message 1", "message-key");
        campaignRecord = CampaignRecordBuilder.cronBasedCampaignRecord("Campaign 1", messageRecord);

        Campaign campaign = campaignRecord.build();
        assertTrue(campaign instanceof CronBasedCampaign);
        CronBasedCampaign cronBasedCampaign = (CronBasedCampaign) campaign;
        assertEquals(campaignRecord.getName(), cronBasedCampaign.getName());
        List<CronBasedCampaignMessage> messages = cronBasedCampaign.getMessages();
        assertEquals(1, messages.size());

        CronBasedCampaignMessage message = messages.get(0);
        assertEquals(messageRecord.getName(), message.name());
        assertEquals(messageRecord.getFormats(), message.formats());
        assertEquals(messageRecord.getLanguages(), message.languages());
        assertEquals(messageRecord.getMessageKey(), message.messageKey());
        assertEquals(messageRecord.getCron(), message.cron());
    }
}
