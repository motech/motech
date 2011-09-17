package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.Time;
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

public abstract class MessageCampaignScheduler<T extends CampaignMessage> {
    protected MotechSchedulerService schedulerService;
    protected CampaignRequest campaignRequest;
    protected Campaign<T> campaign;

    protected MessageCampaignScheduler(MotechSchedulerService schedulerService, CampaignRequest campaignRequest, Campaign<T> campaign) {
        this.schedulerService = schedulerService;
        this.campaign = campaign;
        this.campaignRequest = campaignRequest;
    }

    public void start() {
        for (CampaignMessage message : campaign.messages())
            scheduleJobFor(message);
    }

    public void stop() {
        String jobIdPrefix = String.format("%s%s.%s", EventKeys.BASE_SUBJECT, campaign.name(), campaignRequest.externalId());
        schedulerService.unscheduleAllJobs(jobIdPrefix);
    }

    public void restart() {
        stop();
        start();
    }

    protected abstract void scheduleJobFor(CampaignMessage message);

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

    protected LocalDate referenceDate() {
        return campaignRequest.referenceDate() != null ? campaignRequest.referenceDate() : DateUtil.today();
    }

    protected HashMap jobParams(String messageKey) {
        String jobId = String.format("%s%s.%s.%s", EventKeys.BASE_SUBJECT, campaign.name(), campaignRequest.externalId(), messageKey);
        return new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withMessageKey(messageKey)
                .withExternalId(campaignRequest.externalId())
                .payload();
    }
}

