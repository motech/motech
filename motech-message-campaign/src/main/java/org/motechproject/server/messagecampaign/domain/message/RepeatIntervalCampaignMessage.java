package org.motechproject.server.messagecampaign.domain.message;

import org.joda.time.Period;

public class RepeatIntervalCampaignMessage extends CampaignMessage {

    private Period repeatInterval;

    public RepeatIntervalCampaignMessage(Period repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public long getRepeatIntervalInMillis() {
        return repeatInterval.toStandardSeconds().getSeconds() * 1000;
    }
}
