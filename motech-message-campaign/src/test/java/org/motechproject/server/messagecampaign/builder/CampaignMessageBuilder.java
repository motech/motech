package org.motechproject.server.messagecampaign.builder;

import org.joda.time.LocalDate;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import java.util.Arrays;
import java.util.List;

public class CampaignMessageBuilder {

    public AbsoluteCampaignMessage absoluteCampaignMessage(String name, LocalDate date, String messageKey) {
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

    public RepeatingCampaignMessage repeatingCampaignMessage(String name, String repeatInterval, List<String> weekDays,String messageKey) {
        RepeatingCampaignMessage repeatingCampaignMessage = new RepeatingCampaignMessage(repeatInterval, weekDays);
        repeatingCampaignMessage.name(name);
        repeatingCampaignMessage.formats(Arrays.asList("IVR"));
        repeatingCampaignMessage.languages(Arrays.asList("en"));
        repeatingCampaignMessage.repeatInterval(repeatInterval);
        repeatingCampaignMessage.messageKey(messageKey);
        return repeatingCampaignMessage;
    }
}
