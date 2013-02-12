package org.motechproject.messagecampaign.domain.message;

import org.joda.time.Period;

public class RepeatIntervalCampaignMessage extends CampaignMessage {

    private Period repeatInterval;

    public RepeatIntervalCampaignMessage(Period repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public long getRepeatIntervalInMillis() {
        final int millisInASec = 1000;
        return repeatInterval.toStandardSeconds().getSeconds() * millisInASec;
    }
}
