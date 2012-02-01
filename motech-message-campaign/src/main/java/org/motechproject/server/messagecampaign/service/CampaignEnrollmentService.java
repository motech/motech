package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignEnrollmentService {

    @Autowired
    AllCampaignEnrollments allCampaignEnrollments;

    public void saveOrUpdate(CampaignEnrollment enrollment) {
        allCampaignEnrollments.saveOrUpdate(enrollment);   
    }
}
