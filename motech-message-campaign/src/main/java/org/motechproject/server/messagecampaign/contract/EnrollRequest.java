package org.motechproject.server.messagecampaign.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class EnrollRequest {
    private String externalId;
    private String campaignName;
    private Time reminderTime;
    private LocalDate referenceDate;

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

    public LocalDate referenceDate() {
        return referenceDate;
    }

    public void referenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }
}
