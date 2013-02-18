package org.motechproject.server.messagecampaign.handler;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageCampaignEventHandler {

    @Autowired
    private MessageCampaignService messageCampaignService;

    @MotechListener(subjects = {EventKeys.ENROLL_USER_SUBJECT, EventKeys.UNENROLL_USER_SUBJECT})
    public void enrollOrUnenroll(MotechEvent event) {
        String externalId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String campaignName = event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY).toString();
        LocalDate referenceDate = (LocalDate)event.getParameters().get(EventKeys.REFERENCE_DATE);
        Time referenceTime = new Time(event.getParameters().get(EventKeys.REFERENCE_TIME).toString());
        Time startTime = new Time(event.getParameters().get(EventKeys.START_TIME).toString());
        CampaignRequest request = new CampaignRequest(externalId, campaignName, referenceDate, referenceTime, startTime);

        if (event.getSubject().equals(EventKeys.ENROLL_USER_SUBJECT)) {
            messageCampaignService.startFor(request);
        } else if  (event.getSubject().equals(EventKeys.UNENROLL_USER_SUBJECT)) {
            messageCampaignService.stopAll(request);
        }

    }
}