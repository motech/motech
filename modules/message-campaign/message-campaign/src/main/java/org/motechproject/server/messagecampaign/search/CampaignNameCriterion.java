package org.motechproject.server.messagecampaign.search;

import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.ArrayList;
import java.util.List;

public class CampaignNameCriterion implements Criterion {
    private String campaignName;

    public CampaignNameCriterion(String campaignName) {
        this.campaignName = campaignName;
    }

    @Override
    public List<CampaignEnrollment> fetch(AllCampaignEnrollments allCampaignEnrollments) {
        return allCampaignEnrollments.findByCampaignName(campaignName);
    }

    @Override
    public List<CampaignEnrollment> filter(List<CampaignEnrollment> campaignEnrollments) {
        List<CampaignEnrollment> filteredEnrollments = new ArrayList<>();
        for(CampaignEnrollment enrollment: campaignEnrollments) {
            if (campaignName.equals(enrollment.getCampaignName())) {
                filteredEnrollments.add(enrollment);
            }
        }
        return filteredEnrollments;
    }
}

