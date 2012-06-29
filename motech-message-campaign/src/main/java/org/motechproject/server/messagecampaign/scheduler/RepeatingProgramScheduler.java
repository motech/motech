package org.motechproject.server.messagecampaign.scheduler;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.messagecampaign.Constants;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.valueobjects.WallTime;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.lang.String.format;
import static org.motechproject.util.DateUtil.endOfDay;
import static org.motechproject.util.DateUtil.newDateTime;

public class RepeatingProgramScheduler extends MessageCampaignScheduler<RepeatingCampaignMessage, RepeatingCampaign> {

    public Boolean dispatchMessagesEvery24Hours;

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, RepeatingCampaign campaign,
                                     CampaignEnrollmentService campaignEnrollmentService, Boolean dispatchMessagesEvery24Hours) {
        super(schedulerService, enrollRequest, campaign, campaignEnrollmentService);
        this.dispatchMessagesEvery24Hours = dispatchMessagesEvery24Hours;
    }


    @Override
    protected void scheduleJobFor(RepeatingCampaignMessage message) {
        Boolean repeatIntervalLessThanDay = !dispatchMessagesEvery24Hours && message.repeatIntervalIsLessThanDay();
        WallTime maxDuration = new WallTime(campaign.maxDuration());
        LocalDate startDate = referenceDate();
        Date endDate = repeatIntervalLessThanDay ?
                newDateTime(startDate, getDeliveryTime(message)).plusMinutes(maxDuration.inMinutes()).toDate() :
                endOfDay(startDate.plusDays(message.durationInDaysToAdd(maxDuration, campaignRequest)).toDate()).toDate();

        if (startDate.toDate().compareTo(endDate) > 0) {
            throw new IllegalArgumentException(format("startDate (%s) is after endDate (%s) for - (%s)", startDate, endDate, campaignRequest));
        }

        HashMap<String, Object> params = jobParams(message.messageKey());
        params.put(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY, dispatchMessagesEvery24Hours);

        // Bug 0058
        if (badFunctionConditionToBeRefactored(message)) {
            Integer offsetInDays = campaignRequest.startOffset();
            if (offsetInDays == null)
                offsetInDays = new Integer(0);
            scheduleRepeatingJob(message, params, newDateTime(startDate.plusDays(offsetInDays), message.deliverTime()).toDate(), endDate);
            return;
        }

        if (repeatIntervalLessThanDay) {
            scheduleRepeatingJob(startDate, getDeliveryTime(message), endDate, params, getRepeatIntervalInMilliSeconds(message));
        } else {
            scheduleRepeatingJob(startDate, getDeliveryTime(message), endDate, params, getCronExpression(message));
        }
    }

    private boolean badFunctionConditionToBeRefactored(RepeatingCampaignMessage message) {
        return RepeatingMessageMode.REPEAT_INTERVAL.name().equals(message.mode().name()) && !dispatchMessagesEvery24Hours;
    }

    @Override
    protected String getCampaignMessageSubject(RepeatingCampaignMessage repeatingCampaignMessage) {
        if (badFunctionConditionToBeRefactored(repeatingCampaignMessage)) return EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;
        return INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;
    }

    private void scheduleRepeatingJob(RepeatingCampaignMessage message, Map<String, Object> params, Date startDate, Date endDate) {
        long MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);
        long repeatInterval = message.repeatIntervalForSchedule() * MILLIS_IN_A_DAY;
        if (message.repeatIntervalIsLessThanDay())
            repeatInterval = getRepeatIntervalInMilliSeconds(message).intValue();
        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(motechEvent, startDate, endDate, repeatInterval));
    }

    @Override
    protected DateTime getCampaignEnd() {
        int maxDuration = new WallTime(campaign.maxDuration()).inDays();
        return newDateTime(campaignRequest.referenceDate().plusDays(maxDuration));
    }

    private Time getDeliveryTime(RepeatingCampaignMessage message) {
        if (dispatchMessagesEvery24Hours) {
            return campaignRequest.reminderTime();
        }
        if (campaignRequest.deliverTime() != null) {
            return campaignRequest.deliverTime();
        }
        return message.deliverTime();
    }

    private String getCronExpression(RepeatingCampaignMessage message) {
        if (dispatchMessagesEvery24Hours) {
            return String.format("0 %d %d %s * ? *", campaignRequest.reminderTime().getMinute(),
                    campaignRequest.reminderTime().getHour(), Constants.DAILY_REPEATING_DAYS_CRON_EXPRESSION);
        }

        String deliverDates = StringUtils.join(getShortNames(campaignRequest.getUserPreferredDays().isEmpty() ? message.weekDaysApplicable() : campaignRequest.getUserPreferredDays()).iterator(), ",");
        return String.format("0 %d %d ? * %s *", getDeliveryTime(message).getMinute(), getDeliveryTime(message).getHour(), deliverDates);
    }

    private Long getRepeatIntervalInMilliSeconds(RepeatingCampaignMessage message) {
        WallTime repeatInterval = new WallTime(message.repeatInterval());

        return repeatInterval.inMillis();
    }

    private void scheduleRepeatingJob(LocalDate startDate, Time deliverTime, Date endDate, Map<String, Object> params, String cronExpression) {
        MotechEvent motechEvent = new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, params);
        Date startDateAsDate = (startDate == null) ? null : newDateTime(startDate, deliverTime).withMillisOfSecond(0).toDate();

        schedulerService.safeScheduleJob(new CronSchedulableJob(motechEvent, cronExpression, startDateAsDate, endDate));
    }

    private void scheduleRepeatingJob(LocalDate startDate, Time deliverTime, Date endDate, Map<String, Object> params, Long repeatIntervalInMilliSeconds) {
        MotechEvent motechEvent = new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, params);
        Date startDateAsDate = (startDate == null) ? null : newDateTime(startDate, deliverTime).withMillisOfSecond(0).toDate();

        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(motechEvent, startDateAsDate, endDate, repeatIntervalInMilliSeconds));
    }

    private List<String> getShortNames(List<DayOfWeek> dayOfWeeks) {
        if (CollectionUtils.isEmpty(dayOfWeeks)) {
            return new ArrayList<String>() {{
                add(Constants.DAILY_REPEATING_DAYS_CRON_EXPRESSION);
            }};
        }
        List<String> shortNames = new ArrayList<String>();
        for (DayOfWeek dayOfWeek : dayOfWeeks) {
            shortNames.add(dayOfWeek.getShortName());
        }
        return shortNames;
    }
}
