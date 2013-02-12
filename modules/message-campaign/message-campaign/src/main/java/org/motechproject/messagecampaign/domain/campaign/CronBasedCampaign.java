package org.motechproject.messagecampaign.domain.campaign;

import org.motechproject.messagecampaign.domain.message.CronBasedCampaignMessage;

public class CronBasedCampaign extends Campaign<CronBasedCampaignMessage> {

    private String maxDuration;

    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String maxDuration() {
        return maxDuration;
    }
}
