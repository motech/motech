package org.motechproject.server.messagecampaign.builder;

import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;

public class EnrollRequestBuilder {

    private String campaignName;
    private Time reminderTime;
    private String externalId;

    public EnrollRequestBuilder withDefaults() {
        campaignName = "testCampaign";
        reminderTime = new Time(9, 30);
        externalId = "12345";
        return this;
    }

    public EnrollRequest build() {
        EnrollRequest request = new EnrollRequest();
        request.campaignName(this.campaignName);
        request.externalId(this.externalId);
        request.reminderTime(this.reminderTime);
        return request;
    }
}
