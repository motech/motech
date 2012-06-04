package org.motechproject.server.messagecampaign.scheduler;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
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
import org.motechproject.valueobjects.WallTime;
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
    public static final String MINUTE = "{Minute}";

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
        String[] nextApplicableTime = getApplicableTime(event, repeatingCampaignMessage);
        String nextApplicableDay = getApplicableDay(event, repeatingCampaignMessage, getNextApplicableHour(nextApplicableTime));

        if (nextApplicableDay != null && nextApplicableTime != null && nextApplicableTime.length == 2) {
            Map<String, Object> params = event.getParameters();
            CampaignEnrollment enrollment = enrollment(params);
            Integer startIntervalOffset = enrollment.startOffset(repeatingCampaignMessage);
            Time deliverTime = repeatingCampaignMessage.deliverTime();
            DateTime startDate = enrollment.getStartDate().toDateTime(new LocalTime(deliverTime.getHour(), deliverTime.getMinute()));

            Integer currentOffset = repeatingCampaignMessage.currentOffset(startDate, startIntervalOffset);
            params.put(EventKeys.GENERATED_MESSAGE_KEY, generateMsgKey(params.get(MESSAGE_KEY).toString(), currentOffset.toString(), nextApplicableDay, nextApplicableTime));

            MotechEvent campaignEvent = event.copy(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getParameters());
            setCampaignLastEvent(repeatingCampaignMessage, campaignEvent);
            outboundEventGateway.sendEventMessage(campaignEvent);
        }
    }

    private String generateMsgKey(String originalMessageKey, String offset, String weekDay, String[] nextApplicableTime) {
        String generatedKey = replace(originalMessageKey, OFFSET, offset);
        generatedKey = replace(generatedKey, WEEK_DAY, weekDay);
        generatedKey = replace(generatedKey, HOUR, nextApplicableTime[0]);

        return replace(generatedKey, MINUTE, nextApplicableTime[1]);
    }

    private String getApplicableDay(MotechEvent event, RepeatingCampaignMessage repeatingCampaignMessage, String nextApplicableHour) {
        Boolean dispatchMessagesEvery24Hours = (Boolean) event.getParameters().get(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY);
        DayOfWeek dayOfWeek;

        if (!dispatchMessagesEvery24Hours) {
            DateTime now = DateUtil.now();

            if(repeatingCampaignMessage.repeatIntervalIsLessThanDay() &&
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

    private String[] getApplicableTime(MotechEvent event, RepeatingCampaignMessage repeatingCampaignMessage) {
        Boolean dispatchMessagesEvery24Hours = (Boolean) event.getParameters().get(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY);
        Integer hour = repeatingCampaignMessage.deliverTime().getHour();
        Integer minute = repeatingCampaignMessage.deliverTime().getMinute();
        String[] applicableTime = { hour.toString(), String.format("%02d", minute) };

        if (!dispatchMessagesEvery24Hours && repeatingCampaignMessage.repeatIntervalIsLessThanDay()) {
            WallTime interval = new WallTime(repeatingCampaignMessage.repeatInterval());
            DateTime then = DateUtil.now().getHourOfDay() < hour ? DateUtil.now().minusDays(1) : DateUtil.now();

            then = then.withHourOfDay(hour).withMinuteOfHour(minute);

            int duration = Minutes.minutesBetween(then, DateUtil.now()).getMinutes();

            if (duration > 0 && duration < interval.inMinutes()) {
                applicableTime = null;
            } else {
                then = then.plusMinutes((duration / interval.inMinutes()) * interval.inMinutes());

                applicableTime = new String[] {
                        String.format("%d", then.getHourOfDay()),
                        String.format("%02d", then.getMinuteOfHour())
                };
            }
        }

        return applicableTime;
    }

    public String getNextApplicableHour(String[] applicableTime) {
        return applicableTime != null && applicableTime.length == 2 ? applicableTime[0] : null;
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
