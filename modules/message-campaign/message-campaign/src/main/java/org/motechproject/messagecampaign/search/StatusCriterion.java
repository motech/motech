package org.motechproject.messagecampaign.search;

import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

import java.util.ArrayList;
import java.util.List;

public class StatusCriterion implements Criterion {
    private CampaignEnrollmentStatus campaignEnrollmentStatus;

    public StatusCriterion(CampaignEnrollmentStatus campaignEnrollmentStatus) {
        this.campaignEnrollmentStatus = campaignEnrollmentStatus;
    }

    @Override
    public List<CampaignEnrollment> fetch(AllCampaignEnrollments allCampaignEnrollments) {
        return allCampaignEnrollments.findByStatus(campaignEnrollmentStatus);
    }

    @Override
    public List<CampaignEnrollment> filter(List<CampaignEnrollment> campaignEnrollments) {
        List<CampaignEnrollment> filteredEnrollments = new ArrayList<>();
        for(CampaignEnrollment enrollment: campaignEnrollments) {
            if (campaignEnrollmentStatus.equals(enrollment.getStatus())) {
                filteredEnrollments.add(enrollment);
            }
        }
        return filteredEnrollments;
    }
}
