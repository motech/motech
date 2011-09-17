package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.CampaignRequest;

public interface MessageCampaignService {
    void startFor(CampaignRequest enrollRequest);
    void restartFor(CampaignRequest enrollRequest);
    void stopFor(CampaignRequest enrollRequest);

}
