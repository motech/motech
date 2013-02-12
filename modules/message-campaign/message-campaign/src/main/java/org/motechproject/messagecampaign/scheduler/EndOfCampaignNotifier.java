package org.motechproject.messagecampaign.scheduler;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EndOfCampaignNotifier {

    private AllCampaignEnrollments allCampaignEnrollments;
    private EventRelay eventRelay;

    @Autowired
    public EndOfCampaignNotifier(AllCampaignEnrollments allCampaignEnrollments, EventRelay eventRelay) {
        this.allCampaignEnrollments = allCampaignEnrollments;
        this.eventRelay = eventRelay;
    }

    @MotechListener(subjects = EventKeys.SEND_MESSAGE)
    public void handle(MotechEvent event) throws SchedulerException {
        String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);

        if (event.isLastEvent()) {
            markEnrollmentAsComplete(externalId, campaignName);

            Map<String, Object> params = new HashMap<>();
            params.put(EventKeys.EXTERNAL_ID_KEY, externalId);
            params.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
            MotechEvent endOfCampaignEvent = new MotechEvent(EventKeys.CAMPAIGN_COMPLETED, params);
            eventRelay.sendEventMessage(endOfCampaignEvent);
        }
    }

    private void markEnrollmentAsComplete(String externalId, String campaignName) {
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        enrollment.setStatus(CampaignEnrollmentStatus.COMPLETED);
        allCampaignEnrollments.update(enrollment);
    }
}
