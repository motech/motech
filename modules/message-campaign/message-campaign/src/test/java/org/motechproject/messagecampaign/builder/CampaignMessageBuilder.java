package org.motechproject.messagecampaign.builder;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.commons.date.util.JodaFormatter;

import java.util.Arrays;

public class CampaignMessageBuilder {

    public AbsoluteCampaignMessage absoluteCampaignMessage(String name, LocalDate date, String messageKey, Time startTime) {
        AbsoluteCampaignMessage absoluteCampaignMessage = new AbsoluteCampaignMessage();
        absoluteCampaignMessage.name(name);
        absoluteCampaignMessage.date(date);
        absoluteCampaignMessage.messageKey(messageKey);
        absoluteCampaignMessage.setStartTime(startTime);
        absoluteCampaignMessage.formats(Arrays.asList("IVR"));
        absoluteCampaignMessage.languages(Arrays.asList("en"));
        return absoluteCampaignMessage;
    }

    public CronBasedCampaignMessage cronBasedCampaignMessage(String name, String cron, String messageKey) {
        CronBasedCampaignMessage cronBasedCampaignMessage = new CronBasedCampaignMessage();
        cronBasedCampaignMessage.name(name);
        cronBasedCampaignMessage.cron(cron);
        cronBasedCampaignMessage.messageKey(messageKey);
        return cronBasedCampaignMessage;
    }

    public OffsetCampaignMessage offsetCampaignMessage(String name, String timeOffset, String messageKey, Time startTime) {
        OffsetCampaignMessage offsetCampaignMessage = new OffsetCampaignMessage();
        offsetCampaignMessage.name(name);
        offsetCampaignMessage.timeOffset(new JodaFormatter().parsePeriod(timeOffset));
        offsetCampaignMessage.messageKey(messageKey);
        offsetCampaignMessage.setStartTime(startTime);
        return offsetCampaignMessage;
    }
}
