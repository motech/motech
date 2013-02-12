package org.motechproject.messagecampaign.domain.campaign;

import org.motechproject.messagecampaign.domain.message.CampaignMessage;

import java.util.List;

public abstract class Campaign<T extends CampaignMessage> {
    private String name;
    private List<T> messages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessages(List<T> messages) {
        this.messages = messages;
    }

    public List<T> getMessages() {
        return messages;
    }
}
