package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;

import java.util.List;

public class OffsetCampaign extends Campaign {
    private List<OffsetCampaignMessage> messages;

    public List<OffsetCampaignMessage> messages() {
        return this.messages;
    }

    public void messages(List<OffsetCampaignMessage> messages) {
        this.messages = messages;
    }

}
