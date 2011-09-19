package org.motechproject.server.messagecampaign.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class CampaignRequest {
    private String externalId;
    private String campaignName;
    private Time reminderTime;
    private LocalDate referenceDate;

    public CampaignRequest() {
    }

    public CampaignRequest(String externalId, String campaignName, Time reminderTime, LocalDate referenceDate) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.reminderTime = reminderTime;
        this.referenceDate = referenceDate;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String campaignName() {
        return this.campaignName;
    }

    public String externalId() {
        return this.externalId;
    }

    public void setReminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Time reminderTime() {
        return this.reminderTime;
    }

    public LocalDate referenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }
}
