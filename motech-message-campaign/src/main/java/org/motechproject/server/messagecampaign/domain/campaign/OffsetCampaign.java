package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;

import java.util.List;

public class OffsetCampaign extends Campaign<OffsetCampaignMessage> {

    private List<OffsetCampaignMessage> messages;

    protected String maxDuration;

    @Override
    public List<OffsetCampaignMessage> messages() {
        return this.messages;
    }

    @Override
    public void messages(List<OffsetCampaignMessage> messages) {
        this.messages = messages;
    }

    public String maxDuration() {
        return maxDuration;
    }


    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }
}
