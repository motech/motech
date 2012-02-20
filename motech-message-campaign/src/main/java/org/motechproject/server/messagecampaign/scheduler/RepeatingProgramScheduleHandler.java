package org.motechproject.server.messagecampaign.scheduler;

import org.apache.log4j.Logger;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.replace;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_KEY;

@Component
public class RepeatingProgramScheduleHandler {

    private static final Logger log = Logger.getLogger(RepeatingProgramScheduleHandler.class);

    public static final String OFFSET = "{Offset}";
    public static final String WEEK_DAY = "{WeekDay}";
    
    private OutboundEventGateway outboundEventGateway;
    private AllMessageCampaigns allMessageCampaigns;
    private CampaignEnrollmentService campaignEnrollmentService;

    @Autowired
    public RepeatingProgramScheduleHandler(OutboundEventGateway outboundEventGateway, AllMessageCampaigns allMessageCampaigns, CampaignEnrollmentService campaignEnrollmentService) {
        this.outboundEventGateway = outboundEventGateway;
        this.allMessageCampaigns = allMessageCampaigns;
        this.campaignEnrollmentService = campaignEnrollmentService;
    }

    @MotechListener(subjects = {RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT})
    public void handleEvent(MotechEvent event) {
        log.info("handled internal repeating campaign event and forwarding: " + event.getParameters().hashCode());

        RepeatingCampaignMessage repeatingCampaignMessage = (RepeatingCampaignMessage) getCampaignMessage(event);
        String nextApplicableDay = repeatingCampaignMessage.applicableWeekDayInNext24Hours();

        if (nextApplicableDay != null) {
            Map<String, Object> params = event.getParameters();
            CampaignEnrollment enrollment = enrollment(params);
            Integer startIntervalOffset = enrollment.startOffset(repeatingCampaignMessage);
            Date startDate = enrollment.getStartDate().toDate();

            Integer offset = repeatingCampaignMessage.currentOffset(startDate, startIntervalOffset);
            replaceMessageKeyParams(params, OFFSET, offset.toString());
            replaceMessageKeyParams(params, WEEK_DAY, nextApplicableDay);

            outboundEventGateway.sendEventMessage(event.copy(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getParameters()));
        }
    }

    private CampaignEnrollment enrollment(Map<String, Object> map) {
        return campaignEnrollmentService.findByExternalIdAndCampaignName(
                (String) map.get(EventKeys.EXTERNAL_ID_KEY), (String) map.get(EventKeys.CAMPAIGN_NAME_KEY));
    }

    private void replaceMessageKeyParams(Map<String, Object> parameters, String parameterName, String value) {
        parameters.put(MESSAGE_KEY, replace(parameters.get(MESSAGE_KEY).toString(), parameterName, value));
    }

    private CampaignMessage getCampaignMessage(MotechEvent motechEvent) {
        String campaignName = (String) motechEvent.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) motechEvent.getParameters().get(EventKeys.MESSAGE_KEY);
        return allMessageCampaigns.get(campaignName, messageKey);
    }
}
