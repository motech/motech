package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;

public class OffsetCampaign extends Campaign<OffsetCampaignMessage> {

    protected String maxDuration;

    public String maxDuration() {
        return maxDuration;
    }

    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }
}
