package org.motechproject.messagecampaign.builder;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.messagecampaign.contract.CampaignRequest;

public class EnrollRequestBuilder {

    private String campaignName;
    private Time deliverTime;
    private String externalId;
    private LocalDate referenceDate;

    public EnrollRequestBuilder withDefaults() {
        campaignName = "testCampaign";
        deliverTime = new Time(9, 30);
        externalId = "12345";
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

    public EnrollRequestBuilder withDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
        return this;
    }

    public CampaignRequest build() {
        CampaignRequest request = new CampaignRequest();
        request.setCampaignName(this.campaignName);
        request.setExternalId(this.externalId);
        request.setReferenceDate(this.referenceDate);
        request.setStartTime(this.deliverTime);
        return request;
    }
}
