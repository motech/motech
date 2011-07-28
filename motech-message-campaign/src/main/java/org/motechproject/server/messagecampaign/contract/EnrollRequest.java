package org.motechproject.server.messagecampaign.contract;

public class EnrollRequest {

    private String externalId;
    private String campaignName;

    public void campaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void externalId(String externalId) {
        this.externalId = externalId;
    }

    public String campaignName() {
        return this.campaignName;
    }

    public String externalId() {
        return this.externalId;
    }
}
