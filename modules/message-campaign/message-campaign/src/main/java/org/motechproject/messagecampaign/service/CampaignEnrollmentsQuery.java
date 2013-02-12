package org.motechproject.messagecampaign.service;

import org.motechproject.messagecampaign.search.Criterion;
import org.motechproject.messagecampaign.search.ExternalIdCriterion;
import org.motechproject.messagecampaign.search.StatusCriterion;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.messagecampaign.search.CampaignNameCriterion;

import java.util.ArrayList;
import java.util.List;

/**
 * \ingroup MessageCampaign
 *
 * This is the Query builder for retrieving campaign enrollments
 * Provides methods for different query criteria
 * The order of criteria matters, as the first criterion is used to fetch the result from database
 * and the other criterion are used to filter the results fetched by the first criterion(in memory)
 *
 */
public class CampaignEnrollmentsQuery {

    private List<Criterion> criteria = new ArrayList<Criterion>();

    /**
     * This provides the method for the Status Criterion using which campaign enrollments are filtered based on their status
     * @param campaignEnrollmentStatus
     * @return
     */
    public CampaignEnrollmentsQuery havingState(CampaignEnrollmentStatus campaignEnrollmentStatus) {
        criteria.add(new StatusCriterion(campaignEnrollmentStatus));
        return this;
    }

    /**
     * This provides the method for the ExternalId Criterion using which campaign enrollments for an ExternalId are filtered
     * @param externalId
     * @return
     */
    public CampaignEnrollmentsQuery withExternalId(String externalId) {
        criteria.add(new ExternalIdCriterion(externalId));
        return this;
    }

    /**
     * This provides the method for the CampaignName Criterion using which campaign enrollments belongs to a particular campaign are filtered
     * @param campaignName
     * @return
     */
    public CampaignEnrollmentsQuery withCampaignName(String campaignName) {
        criteria.add(new CampaignNameCriterion(campaignName));
        return this;
    }

    /**
     * This gives all the criterion which are present in the built query
     * @return List<Criterion>
     */
    public List<Criterion> getCriteria() {
        return criteria;
    }

    /**
     * This gives the primary criterion in the built query, which is used to fetch the results from database
     * @return Criterion
     */
    public Criterion getPrimaryCriterion() {
        return (criteria.size() > 0) ? criteria.get(0) : null;
    }

    /**
     * This gives all the criterion other than primary criterion in the built query, which are used to filter the results of the primary criterion
     * @return List<Criterion>
     */
    public List<Criterion> getSecondaryCriteria() {
        return (criteria.size() > 1) ? criteria.subList(1, criteria.size()) : new ArrayList<Criterion>();
    }
}
