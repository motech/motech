package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.*;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.BASE_SUBJECT;

public abstract class MessageCampaignScheduler<T extends CampaignMessage, E extends Campaign<T>> {
    protected MotechSchedulerService schedulerService;
    protected CampaignRequest campaignRequest;
    protected E campaign;
    final static String INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT = BASE_SUBJECT + "internal-repeating-campaign";

    protected MessageCampaignScheduler(MotechSchedulerService schedulerService, CampaignRequest campaignRequest, E campaign) {
        this.schedulerService = schedulerService;
        this.campaign = campaign;
        this.campaignRequest = campaignRequest;
    }

    public void start() {
        for (T message : campaign.messages())
            scheduleJobFor(message);
    }

    public void stop() {
        String jobIdPrefix = String.format("%s%s.%s", BASE_SUBJECT, campaign.name(), campaignRequest.externalId());
        schedulerService.unscheduleAllJobs(jobIdPrefix);
    }

    public void restart() {
        stop();
        start();
    }

    protected abstract void scheduleJobFor(T message);

    protected void scheduleJobOn(Time startTime, LocalDate startDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);
        Date startDateTime = startDate == null ? null : DateUtil.newDateTime(startDate, startTime.getHour(), startTime.getMinute(), 0).toDate();
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDateTime);
        schedulerService.scheduleRunOnceJob(runOnceSchedulableJob);
    }

    protected void scheduleJobOn(String cronJobExpression, LocalDate startDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);
        Date startDateAsDate = startDate == null ? null : startDate.toDate();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startDateAsDate, null);
        schedulerService.scheduleJob(schedulableJob);
    }

    protected void scheduleRepeatingJob(LocalDate startDate, LocalDate endDate, long repeatInterval, Map<String, Object> params) {
        scheduleRepeatingJob(startDate, endDate, repeatInterval, null, params);
    }

    protected void scheduleRepeatingJob(LocalDate startDate, LocalDate endDate, long repeatInterval, Integer repeatCount, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, params);
        Date startDateAsDate = startDate == null ? null : startDate.toDate();
        Date endDateAsDate = endDate == null ? null : endDate.toDate();
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, startDateAsDate, endDateAsDate, repeatCount, repeatInterval);
        schedulerService.scheduleRepeatingJob(schedulableJob);
    }

    protected LocalDate referenceDate() {
        return campaignRequest.referenceDate() != null ? campaignRequest.referenceDate() : DateUtil.today();
    }

    protected HashMap jobParams(String messageKey) {
        String jobId = String.format("%s%s.%s.%s", BASE_SUBJECT, campaign.name(), campaignRequest.externalId(), messageKey);
        return new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withMessageKey(messageKey)
                .withExternalId(campaignRequest.externalId())
                .payload();
    }
}

