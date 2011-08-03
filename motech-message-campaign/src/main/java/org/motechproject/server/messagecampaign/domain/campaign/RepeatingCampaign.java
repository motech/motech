package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import java.util.List;

public class RepeatingCampaign extends Campaign<RepeatingCampaignMessage> {

    private List<RepeatingCampaignMessage> messages;

    private String maxDuration;

    @Override
    public List<RepeatingCampaignMessage> messages() {
        return this.messages;
    }

    @Override
    public void messages(List<RepeatingCampaignMessage> messages) {
        this.messages = messages;
    }

    public void maxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String maxDuration() {
        return maxDuration;
    }
}
