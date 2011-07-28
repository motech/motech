package org.motechproject.server.messagecampaign.domain;

import java.util.List;

public class Campaign {
    private String name;
    private List<CampaignMessage> messages;
    private String maxDuration;

    public String getName() {
        return name;
    }

    public List<CampaignMessage> getMessages() {
        return messages;
    }

    public String maxDuration() {
        return maxDuration;
    }
}
