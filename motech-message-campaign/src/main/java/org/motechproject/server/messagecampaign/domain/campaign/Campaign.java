package org.motechproject.server.messagecampaign.domain.campaign;

import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.List;

public abstract class Campaign<T extends CampaignMessage> {

    private String name;

    private CampaignType type;

    private List<CampaignMessage> messages;

    public void name(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public CampaignType type() {
        return type;
    }

    public abstract void messages(List<T> messages);

    public abstract List<T> messages();

    public void type(CampaignType type) {
        this.type = type;
    }

    public void addMessage(CampaignMessage message){
        messages.add(message);
    }
}
