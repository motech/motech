package org.motechproject.server.messagecampaign.service;

import org.joda.time.DateTime;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import java.util.List;
import java.util.Map;

/**
 * \ingroup MessageCampaign
 */
public interface MessageCampaignService {
    /** Enrolls the external id into the campaign as specified in the request. The enrolled entity will have events raised against it according to the campaign definition.
     *
     * @param enrollRequest
     */
    void startFor(CampaignRequest enrollRequest);
    /** Unenrolls an external from the campaign as specified in the request. The entity will no longer receive events from the campaign.
     *
     * @param enrollRequest
     */
    void stopAll(CampaignRequest enrollRequest);

    /** Searches and returns the Campaign Enrollment Records as per the criteria in the given CampaignEnrollmentsQuery
     * The query consists of various criteria based on Status, ExternalId and CampaignName of the CampaignEnrollment
     *
     * @param query
     * @return List<CampaignEnrollmentRecord>
     */
    List<CampaignEnrollmentRecord> search(CampaignEnrollmentsQuery query);

    Map<String, List<DateTime>> getCampaignTimings(String externalId, String campaignName, DateTime startDate, DateTime endDate);
}
