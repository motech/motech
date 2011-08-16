package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class MessageCampaignScheduler<T extends CampaignMessage> {
    public static final int REPEAT_WINDOW_IN_HOURS = 2;
    public static final int REPEAT_INTERVAL_IN_MINUTES = 15;

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
    };

    protected abstract void scheduleJob(CampaignMessage message);

    protected HashMap jobParams(CampaignMessage message) {
        return jobParams(message.messageKey());
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

        String cronJobExpression = new CronJobExpressionBuilder(startTime,
                REPEAT_WINDOW_IN_HOURS, REPEAT_INTERVAL_IN_MINUTES).build();

        scheduleJobOn(cronJobExpression, startDate, params);
    }

    protected void scheduleJobOn(String cronJobExpression, LocalDate startDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_EVENT_SUBJECT, params);

        Date startDateAsDate = startDate == null ?  null : startDate.toDate();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startDateAsDate, null);
        schedulerService.scheduleJob(schedulableJob);
    }
}

