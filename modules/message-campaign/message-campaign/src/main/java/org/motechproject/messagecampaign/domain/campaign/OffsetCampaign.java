package org.motechproject.messagecampaign.domain.campaign;

import org.motechproject.messagecampaign.domain.message.OffsetCampaignMessage;

public class OffsetCampaign extends Campaign<OffsetCampaignMessage> {

    private String maxDuration;

    public String maxDuration() {
        return maxDuration;
    }

    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }
}
