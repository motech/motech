package org.motechproject.server.messagecampaign.builder;

import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

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

    public CampaignRequest build() {
        CampaignRequest request = new CampaignRequest();
        request.setCampaignName(this.campaignName);
        request.setExternalId(this.externalId);
        request.setReminderTime(this.reminderTime);
        return request;
    }
}
