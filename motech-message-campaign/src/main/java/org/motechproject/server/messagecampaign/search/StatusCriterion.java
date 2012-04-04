package org.motechproject.server.messagecampaign.search;

import ch.lambdaj.Lambda;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

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
        return Lambda.filter(having(on(CampaignEnrollment.class).getStatus(), equalTo(campaignEnrollmentStatus)), campaignEnrollments);
    }
}
