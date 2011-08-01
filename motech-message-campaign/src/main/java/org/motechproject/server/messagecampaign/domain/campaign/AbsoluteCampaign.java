package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;

import java.util.List;

public class AbsoluteCampaign extends Campaign<AbsoluteCampaignMessage> {

    protected List<AbsoluteCampaignMessage> messages;

    @Override
    public List<AbsoluteCampaignMessage> messages() {
        return this.messages;
    }

    @Override
    public void messages(List<AbsoluteCampaignMessage> messages) {
        this.messages = messages;
    }
}
