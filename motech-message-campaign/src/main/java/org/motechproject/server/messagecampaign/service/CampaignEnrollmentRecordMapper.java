package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.springframework.stereotype.Component;

@Component
public class CampaignEnrollmentRecordMapper {
    public CampaignEnrollmentRecord map(CampaignEnrollment enrollment) {
        if (enrollment == null)
            return null;
        return new CampaignEnrollmentRecord(enrollment.getExternalId(), enrollment.getCampaignName(), enrollment.getStartDate(), enrollment.getStatus());
    }
}
