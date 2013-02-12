package org.motechproject.messagecampaign.service;

import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.springframework.stereotype.Component;

@Component
public class CampaignEnrollmentRecordMapper {
    public CampaignEnrollmentRecord map(CampaignEnrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        return new CampaignEnrollmentRecord(enrollment.getExternalId(), enrollment.getCampaignName(), enrollment.getReferenceDate(), enrollment.getStatus());
    }
}
