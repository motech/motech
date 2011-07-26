package org.motechproject.server.messagecampaign.domain;

import java.util.List;

public class Campaign {
    private String name;
    private List<CampaignMessage> messages;

    public String getName() {
        return name;
    }

    public List<CampaignMessage> getMessages() {
        return messages;
    }
}
