package org.motechproject.server.messagecampaign.contract;

import org.motechproject.model.Time;

public abstract class EnrollRequest {

    private String externalId;
    private String campaignName;
    private Time reminderTime;

    public void campaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void externalId(String externalId) {
        this.externalId = externalId;
    }

    public void reminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String campaignName() {
        return this.campaignName;
    }

    public String externalId() {
        return this.externalId;
    }

    public Time reminderTime() {
        return this.reminderTime;
    }
}
