package org.motechproject.messagecampaign.domain.campaign;

import org.joda.time.Period;
import org.motechproject.messagecampaign.domain.message.RepeatIntervalCampaignMessage;

public class RepeatIntervalCampaign extends Campaign<RepeatIntervalCampaignMessage> {

    private Period maxDuration;

    public RepeatIntervalCampaign name(String name) {
        setName(name);
        return this;
    }

    public RepeatIntervalCampaign maxDuration(Period maxDuration) {
        this.maxDuration = maxDuration;
        return this;
    }

    public Period maxDuration() {
        return maxDuration;
    }
}
