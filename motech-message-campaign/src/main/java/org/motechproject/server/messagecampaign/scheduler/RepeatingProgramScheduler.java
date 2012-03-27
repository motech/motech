package org.motechproject.server.messagecampaign.scheduler;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.Constants;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.valueobjects.WallTime;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.lang.String.format;
import static org.motechproject.util.DateUtil.endOfDay;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.valueobjects.factory.WallTimeFactory.wallTime;

public class RepeatingProgramScheduler extends MessageCampaignScheduler<RepeatingCampaignMessage, RepeatingCampaign> {

    public Boolean dispatchMessagesEvery24Hours;

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, RepeatingCampaign campaign,
                                     CampaignEnrollmentService campaignEnrollmentService, Boolean dispatchMessagesEvery24Hours) {
        super(schedulerService, enrollRequest, campaign, campaignEnrollmentService);
        this.dispatchMessagesEvery24Hours = dispatchMessagesEvery24Hours;
    }


    @Override
    protected void scheduleJobFor(RepeatingCampaignMessage message) {
        WallTime maxDuration = wallTime(campaign.maxDuration());
        LocalDate startDate = referenceDate();
        LocalDate endDate = startDate.plusDays(message.durationInDaysToAdd(maxDuration, campaignRequest));
        Date endDateToEndOfDay = endOfDay(endDate.toDate()).toDate();

        if (startDate.toDate().compareTo(endDateToEndOfDay) > 0) {
            throw new IllegalArgumentException(format("startDate (%s) is after endDate (%s) for - (%s)", startDate, endDateToEndOfDay, campaignRequest));
        }

        HashMap<String, Object> params = jobParams(message.messageKey());
        params.put(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY, dispatchMessagesEvery24Hours);
        scheduleRepeatingJob(startDate, getDeliveryTime(message), endDateToEndOfDay, params, getCronExpression(message));
    }

    @Override
    protected DateTime getCampaignEnd() {
        int maxDuration = wallTime(campaign.maxDuration()).inDays();
        return newDateTime(campaignRequest.referenceDate().plusDays(maxDuration));
    }

    private Time getDeliveryTime(RepeatingCampaignMessage message) {
        return (dispatchMessagesEvery24Hours) ? campaignRequest.reminderTime() : message.deliverTime();
    }

    private String getCronExpression(RepeatingCampaignMessage message) {
        if (dispatchMessagesEvery24Hours)
            return String.format("0 %d %d %s * ? *", campaignRequest.reminderTime().getMinute(),
                    campaignRequest.reminderTime().getHour(), Constants.DAILY_REPEATING_DAYS_CRON_EXPRESSION);

        String deliverDates = StringUtils.join(getShortNames(campaignRequest.getUserPreferredDays().isEmpty() ? message.weekDaysApplicable() : campaignRequest.getUserPreferredDays()).iterator(), ",");

        return String.format("0 %d %d ? * %s *", message.deliverTime().getMinute(), message.deliverTime().getHour(),
                deliverDates);
    }

    private void scheduleRepeatingJob(LocalDate startDate, Time deliverTime, Date endDate, Map<String, Object> params, String cronExpression) {
        MotechEvent motechEvent = new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, params);
        Date startDateAsDate = (startDate == null) ? null : newDateTime(startDate, deliverTime).withMillisOfSecond(0).toDate();
        Date endDateAsDate = (endDate == null) ? null : endDate;
        schedulerService.safeScheduleJob(new CronSchedulableJob(motechEvent, cronExpression, startDateAsDate, endDateAsDate));
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
