package org.motechproject.server.messagecampaign.scheduler;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Hours;
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
import org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.motechproject.valueobjects.factory.WallTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.replace;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_KEY;

@Component
public class RepeatingProgramScheduleHandler {

    private static final Logger log = Logger.getLogger(RepeatingProgramScheduleHandler.class);

    public static final String OFFSET = "{Offset}";
    public static final String WEEK_DAY = "{WeekDay}";
    public static final String HOUR = "{Hour}";

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
        String nextApplicableHour = getApplicableHour(event, repeatingCampaignMessage);
        String nextApplicableDay = getApplicableDay(event, repeatingCampaignMessage, nextApplicableHour);

        if (nextApplicableDay != null && nextApplicableHour != null) {
            Map<String, Object> params = event.getParameters();
            CampaignEnrollment enrollment = enrollment(params);
            Integer startIntervalOffset = enrollment.startOffset(repeatingCampaignMessage);
            DateTime startDate = enrollment.getStartDate().toDateTimeAtStartOfDay().withHourOfDay(repeatingCampaignMessage.deliverTime().getHour());

            Integer currentOffset = repeatingCampaignMessage.currentOffset(startDate, startIntervalOffset);
            params.put(EventKeys.GENERATED_MESSAGE_KEY, generateMsgKey(params.get(MESSAGE_KEY).toString(), currentOffset.toString(), nextApplicableDay, nextApplicableHour));

            MotechEvent campaignEvent = event.copy(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getParameters());
            setCampaignLastEvent(repeatingCampaignMessage, campaignEvent);
            outboundEventGateway.sendEventMessage(campaignEvent);
        }
    }

    private String generateMsgKey(String originalMessageKey, String offset, String weekDay, String hour) {
        String generatedKey = replace(originalMessageKey, OFFSET, offset);
        generatedKey = replace(generatedKey, WEEK_DAY, weekDay);

        return replace(generatedKey, HOUR, hour);
    }

    private String getApplicableDay(MotechEvent event, RepeatingCampaignMessage repeatingCampaignMessage, String nextApplicableHour) {
        Boolean dispatchMessagesEvery24Hours = (Boolean) event.getParameters().get(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY);
        DayOfWeek dayOfWeek;

        if (!dispatchMessagesEvery24Hours) {
            DateTime now = DateUtil.now();

            if(repeatingCampaignMessage.mode() == RepeatingMessageMode.REPEAT_INTERVAL &&
                    WallTimeFactory.wallTime(repeatingCampaignMessage.repeatInterval()).getUnit() == WallTimeUnit.Hour &&
                    now.getHourOfDay() < repeatingCampaignMessage.deliverTime().getHour() &&
                    now.getHourOfDay() < Integer.valueOf(nextApplicableHour)) {
                now = now.minusDays(1);
            }

            dayOfWeek = DayOfWeek.getDayOfWeek(now.toLocalDate().getDayOfWeek());
        } else {
            dayOfWeek = repeatingCampaignMessage.applicableWeekDayInNext24Hours();
        }

        return dayOfWeek != null ? dayOfWeek.name() : null;
    }

    private String getApplicableHour(MotechEvent event, RepeatingCampaignMessage repeatingCampaignMessage) {
        Boolean dispatchMessagesEvery24Hours = (Boolean) event.getParameters().get(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY);
        Integer applicableHour = repeatingCampaignMessage.deliverTime().getHour();

        if (!dispatchMessagesEvery24Hours && repeatingCampaignMessage.mode() == RepeatingMessageMode.REPEAT_INTERVAL) {
            WallTime time = WallTimeFactory.wallTime(repeatingCampaignMessage.repeatInterval());

            if (time.getUnit() == WallTimeUnit.Hour) {
                applicableHour = nextApplicableHour(applicableHour, time.inHours());
            }
        }

        return applicableHour != null ? applicableHour.toString() : null;
    }

    private Integer nextApplicableHour(Integer deliverHour, int interval) {
        DateTime start = (DateUtil.now().getHourOfDay() < deliverHour ? DateUtil.now().minusDays(1) : DateUtil.now()).withHourOfDay(deliverHour);
        Integer hoursInterval = Hours.hoursBetween(start, DateUtil.now()).getHours();
        int count = hoursInterval / interval;

        return hoursInterval == 0 ? deliverHour : (count > 0 ? (deliverHour + (count * interval)) % 24 : null);
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
