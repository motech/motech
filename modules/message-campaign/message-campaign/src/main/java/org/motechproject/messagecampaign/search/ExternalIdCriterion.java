package org.motechproject.messagecampaign.search;

import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.ArrayList;
import java.util.List;

public class ExternalIdCriterion implements Criterion {
    private String externalId;

    public ExternalIdCriterion(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public List<CampaignEnrollment> fetch(AllCampaignEnrollments allCampaignEnrollments) {
        return allCampaignEnrollments.findByExternalId(externalId);
    }

    @Override
    public List<CampaignEnrollment> filter(List<CampaignEnrollment> campaignEnrollments) {
        List<CampaignEnrollment> filteredEnrollment = new ArrayList<>();
        for (CampaignEnrollment enrollment : campaignEnrollments) {
            if (externalId.equals(enrollment.getExternalId())) {
                filteredEnrollment.add(enrollment);
            }
        }
        return filteredEnrollment;
    }
}
