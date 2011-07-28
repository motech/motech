package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.EnrollRequest;

public interface MessageCampaignService {

    void enroll(EnrollRequest enrollRequest);

}
