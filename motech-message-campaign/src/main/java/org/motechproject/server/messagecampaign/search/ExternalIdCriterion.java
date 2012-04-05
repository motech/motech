package org.motechproject.server.messagecampaign.search;

import ch.lambdaj.Lambda;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

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
        return Lambda.filter(having(on(CampaignEnrollment.class).getExternalId(), equalTo(externalId)), campaignEnrollments);
    }
}
