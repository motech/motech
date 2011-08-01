package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;

import java.util.List;

public class CronBasedCampaign extends Campaign<CronBasedCampaignMessage> {

    private List<CronBasedCampaignMessage> messages;

    @Override
    public List<CronBasedCampaignMessage> messages() {
        return this.messages;
    }

    @Override
    public void messages(List<CronBasedCampaignMessage> messages) {
        this.messages = messages;
    }
}
