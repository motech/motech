package org.motechproject.server.messagecampaign.contract;

import org.motechproject.model.Time;

import java.util.Date;

public class EnrollRequest {

    private String externalId;
    private String campaignName;
    private Time reminderTime;
    private Date referenceDate;

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

    public void reminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Time reminderTime() {
        return this.reminderTime;
    }

    public Date referenceDate() {
        if(referenceDate != null)
            return referenceDate;
        return new Date();
    }

    public void referenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
    }
}
