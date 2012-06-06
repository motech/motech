package org.motechproject.server.messagecampaign.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import java.util.ArrayList;
import java.util.List;

public class EnrollRequestBuilder {

    private String campaignName;
    private Time reminderTime;
    private Time deliverTime;
    private String externalId;
    private LocalDate referenceDate;
    private Integer startOffset;
    private List<DayOfWeek> userPreferredDays;

    public EnrollRequestBuilder withDefaults() {
        campaignName = "testCampaign";
        reminderTime = new Time(9, 30);
        externalId = "12345";
        userPreferredDays = new ArrayList<DayOfWeek>();
        return this;
    }

    public EnrollRequestBuilder withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public EnrollRequestBuilder withReferenceDate(LocalDate date) {
        this.referenceDate = date;
        return this;
    }

    public EnrollRequestBuilder withStartOffset(int offset) {
        this.startOffset = offset;
        return this;
    }

    public EnrollRequestBuilder withReminderTime(Time reminderTime) {
        this.reminderTime = reminderTime;
        return this;
    }

    public EnrollRequestBuilder withUserSpecifiedDays(List<DayOfWeek> userSpecifiedDays) {
        this.userPreferredDays = userSpecifiedDays;
        return this;
    }

    public EnrollRequestBuilder withDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
        return this;
    }

    public CampaignRequest build() {
        CampaignRequest request = new CampaignRequest();
        request.setCampaignName(this.campaignName);
        request.setExternalId(this.externalId);
        request.setReminderTime(this.reminderTime);
        request.setReferenceDate(this.referenceDate);
        request.setStartOffset(this.startOffset);
        request.setUserPreferredDays(this.userPreferredDays);
        request.setDeliverTime(this.deliverTime);
        return request;
    }
}
