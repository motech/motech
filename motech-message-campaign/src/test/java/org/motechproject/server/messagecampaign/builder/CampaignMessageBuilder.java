package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import java.util.Date;

public class CampaignMessageBuilder {

    public AbsoluteCampaignMessage absoluteCampaignMessage(String name, Date date, String messageKey) {
        AbsoluteCampaignMessage absoluteCampaignMessage = new AbsoluteCampaignMessage();
        absoluteCampaignMessage.name(name);
        absoluteCampaignMessage.date(date);
        absoluteCampaignMessage.messageKey(messageKey);
        return absoluteCampaignMessage;
    }

    public CronBasedCampaignMessage cronBasedCampaignMessage(String name, String cron, String messageKey) {
        CronBasedCampaignMessage cronBasedCampaignMessage = new CronBasedCampaignMessage();
        cronBasedCampaignMessage.name(name);
        cronBasedCampaignMessage.cron(cron);
        cronBasedCampaignMessage.messageKey(messageKey);
        return cronBasedCampaignMessage;
    }

    public OffsetCampaignMessage offsetCampaignMessage(String name, String timeOffset, String messageKey) {
        OffsetCampaignMessage offsetCampaignMessage = new OffsetCampaignMessage();
        offsetCampaignMessage.name(name);
        offsetCampaignMessage.timeOffset(timeOffset);
        offsetCampaignMessage.messageKey(messageKey);
        return offsetCampaignMessage;
    }

    public RepeatingCampaignMessage repeatingCampaignMessage(String name, String repeatInterval, String messageKey) {
        RepeatingCampaignMessage repeatingCampaignMessage = new RepeatingCampaignMessage();
        repeatingCampaignMessage.name(name);
        repeatingCampaignMessage.repeatInterval(repeatInterval);
        repeatingCampaignMessage.messageKey(messageKey);
        return repeatingCampaignMessage;
    }
}
