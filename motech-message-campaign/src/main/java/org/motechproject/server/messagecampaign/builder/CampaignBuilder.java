package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;

import java.util.ArrayList;
import java.util.List;

public class CampaignBuilder {

    private String name;
    private List<CampaignMessageBuilder> messageBuilders;

    public String name() {
        return this.name;
    }

    public Campaign build() {
        AbsoluteCampaign campaign = new AbsoluteCampaign();
        campaign.name(this.name());
        campaign.messages(buildCampaignMessages());
        return campaign;
    }

    private List<AbsoluteCampaignMessage> buildCampaignMessages() {
        ArrayList<AbsoluteCampaignMessage> absoluteCampaignMessages = new ArrayList<AbsoluteCampaignMessage>();
        for(CampaignMessageBuilder messageBuilder : this.messageBuilders()) {
            absoluteCampaignMessages.add((AbsoluteCampaignMessage) messageBuilder.build());
        }
        return absoluteCampaignMessages;
    }

    private List<CampaignMessageBuilder> messageBuilders() {
        return this.messageBuilders;
    }

    public void name(String name) {
        this.name = name;
    }

    public void messages(ArrayList<CampaignMessageBuilder> campaignMessageBuilders) {
        this.messageBuilders = campaignMessageBuilders;
    }
}
