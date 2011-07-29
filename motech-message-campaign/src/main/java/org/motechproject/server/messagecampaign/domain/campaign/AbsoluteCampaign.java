package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;

import java.util.List;

public class AbsoluteCampaign extends Campaign {

    protected List<AbsoluteCampaignMessage> messages;

    public List<AbsoluteCampaignMessage> messages() {
        return this.messages;
    }

    public void messages(List<AbsoluteCampaignMessage> messages) {
        this.messages = messages;
    }

}
