package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndOfCampaignHandler {

    private AllCampaignEnrollments allCampaignEnrollments;

    @Autowired
    public EndOfCampaignHandler(AllCampaignEnrollments allCampaignEnrollments){
        this.allCampaignEnrollments = allCampaignEnrollments;
    }

    @MotechListener(subjects = EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT)
    public void handle(MotechEvent event) {
        CampaignEnrollment enrollment = (CampaignEnrollment)event.getParameters().get(EventKeys.ENROLLMENT_KEY);
        enrollment.setStatus(CampaignEnrollmentStatus.COMPLETED);
        allCampaignEnrollments.update(enrollment);
    }
}
