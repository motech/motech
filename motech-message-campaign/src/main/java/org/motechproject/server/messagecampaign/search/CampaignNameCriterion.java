package org.motechproject.server.messagecampaign.search;

import ch.lambdaj.Lambda;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

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
        return Lambda.filter(having(on(CampaignEnrollment.class).getCampaignName(), equalTo(campaignName)), campaignEnrollments);
    }
}

