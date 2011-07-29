package org.motechproject.server.messagecampaign.domain.campaign;

public class Campaign {

    private String name;
    private String maxDuration;

    public void name(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public String maxDuration() {
        return maxDuration;
    }

    public CampaignType type() {
        return CampaignType.ABSOLUTE;
    }

}
