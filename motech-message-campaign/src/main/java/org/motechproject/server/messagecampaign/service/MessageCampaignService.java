package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import java.util.List;

/**
 * \ingroup MessageCampaign
 */
public interface MessageCampaignService {
    /** Enrolls the external id into the campaign as specified in the request. The enrolled entity will have events raised against it according to the campaign definition.
     *
     * @param enrollRequest
     */
    void startFor(CampaignRequest enrollRequest);
    void stopFor(CampaignRequest enrollRequest, String message);
    void stopAll(CampaignRequest enrollRequest);

    /** Searches and returns the Campaign Enrollment Records as per the criteria in the given CampaignEnrollmentsQuery
     * The query consists of various criteria based on Status, ExternalId and CampaignName of the CampaignEnrollment
     *
     * @param query
     * @return List<CampaignEnrollmentRecord>
     */
    List<CampaignEnrollmentRecord> search(CampaignEnrollmentsQuery query);

}
