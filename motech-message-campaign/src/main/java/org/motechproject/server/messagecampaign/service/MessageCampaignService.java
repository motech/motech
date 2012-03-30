package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public interface MessageCampaignService {
    /** Enrolls the external id into the campaign as specified in the request. The enrolled entity will have events raised against it according to the campaign definition.
     *
     * @param enrollRequest
     */
    void startFor(CampaignRequest enrollRequest);
    void stopFor(CampaignRequest enrollRequest, String message);
    void stopAll(CampaignRequest enrollRequest);
}
