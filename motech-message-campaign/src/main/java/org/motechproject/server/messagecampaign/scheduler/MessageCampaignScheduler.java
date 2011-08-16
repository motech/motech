package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class MessageCampaignScheduler<T extends CampaignMessage> {
    protected MotechSchedulerService schedulerService;
    protected EnrollRequest enrollRequest;
    protected Campaign<T> campaign;

    protected MessageCampaignScheduler(MotechSchedulerService schedulerService, EnrollRequest enrollRequest, Campaign<T> campaign) {
        this.schedulerService = schedulerService;
        this.campaign = campaign;
        this.enrollRequest = enrollRequest;
    }

    public void scheduleJobs() {
        for (CampaignMessage message : campaign.messages()) {
            scheduleJob(message);
        }
    }

    public void rescheduleJobs() {
        String jobIdPrefix = String.format("%s%s.%s", EventKeys.BASE_SUBJECT, campaign.name(), enrollRequest.externalId());
        schedulerService.unscheduleAllJobs(jobIdPrefix);
        scheduleJobs();
    }

    protected abstract void scheduleJob(CampaignMessage message);

    protected HashMap jobParams(CampaignMessage message) {
        return jobParams(message.messageKey());
    }

    protected LocalDate referenceDate() {
        if(enrollRequest.referenceDate() != null) {
            return enrollRequest.referenceDate();
        }
        return DateUtil.today();
    }

    protected HashMap jobParams(String messageKey) {
        String jobId = String.format("%s%s.%s.%s", EventKeys.BASE_SUBJECT, campaign.name(), enrollRequest.externalId(), messageKey);

        return new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withMessageKey(messageKey)
                .withExternalId(enrollRequest.externalId())
                .payload();
    }

    protected void scheduleJobOn(Time startTime, LocalDate startDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);

        Date startDateTime = startDate == null ? null : DateUtil.newDateTime(startDate, startTime.getHour(), startTime.getMinute(), 0).toDate();
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDateTime);
        schedulerService.scheduleRunOnceJob(runOnceSchedulableJob);
    }

    protected void scheduleJobOn(String cronJobExpression, LocalDate startDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);

        Date startDateAsDate = startDate == null ?  null : startDate.toDate();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startDateAsDate, null);
        schedulerService.scheduleJob(schedulableJob);
    }
}

