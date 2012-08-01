package org.motechproject.server.messagecampaign.search;

import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.List;

public interface Criterion  {
    List<CampaignEnrollment> fetch(AllCampaignEnrollments allCampaignEnrollments);
    List<CampaignEnrollment> filter(List<CampaignEnrollment> campaignEnrollments);
}


