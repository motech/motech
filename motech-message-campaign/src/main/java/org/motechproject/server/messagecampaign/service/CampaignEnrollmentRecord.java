package org.motechproject.server.messagecampaign.service;

import org.joda.time.LocalDate;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

public class CampaignEnrollmentRecord {
    private String externalId;
    private String campaignName;
    private LocalDate startDate;
    private CampaignEnrollmentStatus status;

    public CampaignEnrollmentRecord(String externalId, String campaignName, LocalDate startDate, CampaignEnrollmentStatus status) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.startDate = startDate;
        this.status = status;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public CampaignEnrollmentStatus getStatus() {
        return status;
    }
}
