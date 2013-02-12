package org.motechproject.messagecampaign.domain.campaign;

import org.joda.time.Period;
import org.motechproject.messagecampaign.domain.message.DayOfWeekCampaignMessage;

public class DayOfWeekCampaign extends Campaign<DayOfWeekCampaignMessage> {

    private Period maxDuration;

    public DayOfWeekCampaign name(String name) {
        setName(name);
        return this;
    }

    public DayOfWeekCampaign maxDuration(Period maxDuration) {
        this.maxDuration = maxDuration;
        return this;
    }

    public Period maxDuration() {
        return maxDuration;
    }
}
