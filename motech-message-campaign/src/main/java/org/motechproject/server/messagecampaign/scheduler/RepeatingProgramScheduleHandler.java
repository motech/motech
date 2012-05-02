package org.motechproject.server.messagecampaign.scheduler;

import org.apache.log4j.Logger;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.Constants;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
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
        String nextApplicableDay = getApplicableDay(event, repeatingCampaignMessage);

        if (nextApplicableDay != null) {
            Map<String, Object> params = event.getParameters();
            CampaignEnrollment enrollment = enrollment(params);
            Integer startIntervalOffset = enrollment.startOffset(repeatingCampaignMessage);
            Date startDate = enrollment.getStartDate().toDate();

            Integer currentOffset = repeatingCampaignMessage.currentOffset(startDate, startIntervalOffset);
            params.put(EventKeys.GENERATED_MESSAGE_KEY, generateMsgKey(params.get(MESSAGE_KEY).toString(), currentOffset.toString(), nextApplicableDay));

            MotechEvent campaignEvent = event.copy(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getParameters());
            setCampaignLastEvent(repeatingCampaignMessage, campaignEvent);
            outboundEventGateway.sendEventMessage(campaignEvent);
        }
    }

    private String generateMsgKey(String originalMessageKey, String offset, String weekDay) {
        String generatedKey = replace(originalMessageKey, OFFSET, offset);
        return replace(generatedKey, WEEK_DAY, weekDay);
    }

    private String getApplicableDay(MotechEvent event, RepeatingCampaignMessage repeatingCampaignMessage) {
        DayOfWeek dayOfWeek = (!(Boolean) event.getParameters().get(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY)) ?
                DayOfWeek.getDayOfWeek(DateUtil.now().toLocalDate().getDayOfWeek()) :
                repeatingCampaignMessage.applicableWeekDayInNext24Hours();
        if (dayOfWeek != null) {
            return dayOfWeek.name();
        }
        return null;
    }

    private void setCampaignLastEvent(RepeatingCampaignMessage message, MotechEvent eventToSend) {
        if (!eventToSend.isLastEvent()) {
            eventToSend.setLastEvent(message.hasEnded(eventToSend.getEndTime()));
        }
    }

    private CampaignEnrollment enrollment(Map<String, Object> map) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery().withExternalId((String) map.get(EventKeys.EXTERNAL_ID_KEY)).withCampaignName((String) map.get(EventKeys.CAMPAIGN_NAME_KEY));
        List<CampaignEnrollment> filteredEnrollments = campaignEnrollmentService.search(query);
        return filteredEnrollments.get(0);
    }

    private CampaignMessage getCampaignMessage(MotechEvent motechEvent) {
        String campaignName = (String) motechEvent.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) motechEvent.getParameters().get(EventKeys.MESSAGE_KEY);
        return allMessageCampaigns.get(campaignName, messageKey);
    }

}
