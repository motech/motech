package org.motechproject.server.messagecampaign.search;

import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.List;

public interface Criterion  {
    public abstract List<CampaignEnrollment> fetch(AllCampaignEnrollments allCampaignEnrollments);
    public abstract List<CampaignEnrollment> filter(List<CampaignEnrollment> campaignEnrollments);
}


