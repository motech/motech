package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.search.CampaignNameCriterion;
import org.motechproject.server.messagecampaign.search.Criterion;
import org.motechproject.server.messagecampaign.search.ExternalIdCriterion;
import org.motechproject.server.messagecampaign.search.StatusCriterion;

import java.util.ArrayList;
import java.util.List;

public class CampaignEnrollmentsQuery {

    private List<Criterion> criteria = new ArrayList<Criterion>();

    public CampaignEnrollmentsQuery havingState(CampaignEnrollmentStatus campaignEnrollmentStatus) {
        criteria.add(new StatusCriterion(campaignEnrollmentStatus));
        return this;
    }


    public CampaignEnrollmentsQuery withExternalId(String externalId) {
        criteria.add(new ExternalIdCriterion(externalId));
        return this;
    }

    public CampaignEnrollmentsQuery withCampaignName(String campaignName) {
        criteria.add(new CampaignNameCriterion(campaignName));
        return this;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public Criterion getPrimaryCriterion() {
        return (criteria.size() > 0) ? criteria.get(0) : null;
    }

    public List<Criterion> getSecondaryCriteria() {
        return (criteria.size() > 1) ? criteria.subList(1, criteria.size()) : new ArrayList<Criterion>();
    }
}
