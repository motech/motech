package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.BASE_SUBJECT;

public abstract class MessageCampaignScheduler<T extends CampaignMessage, E extends Campaign<T>> {
    protected MotechSchedulerService schedulerService;
    protected CampaignRequest campaignRequest;
    private CampaignEnrollmentService campaignEnrollmentService;
    protected E campaign;
    public final static String INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT = BASE_SUBJECT + "internal-repeating-campaign";
    private JobIdFactory jobIdFactory;

    protected MessageCampaignScheduler(MotechSchedulerService schedulerService, CampaignRequest campaignRequest, E campaign, CampaignEnrollmentService campaignEnrollmentService) {
        this.schedulerService = schedulerService;
        this.campaign = campaign;
        this.campaignRequest = campaignRequest;
        this.campaignEnrollmentService = campaignEnrollmentService;
        jobIdFactory = new JobIdFactory();
    }

    public void start() {
        CampaignEnrollment enrollment = new CampaignEnrollment(campaignRequest.externalId(), campaignRequest.campaignName())
                .setStartDate(referenceDate()).setStartOffset(campaignRequest.startOffset());
        campaignEnrollmentService.register(enrollment);

        for (T message : campaign.messages())
            scheduleJobFor(message);

        scheduleCompletionJob(enrollment);
    }

    private void scheduleCompletionJob(CampaignEnrollment enrollment) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.ENROLLMENT_KEY, enrollment);
        parameters.put(EventKeys.SCHEDULE_JOB_ID_KEY, jobIdFactory.getCampaignCompletedJobIdFor(campaign.name(), campaignRequest.externalId()));
        MotechEvent event = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT, parameters);
        schedulerService.safeScheduleRunOnceJob(new RunOnceSchedulableJob(event, getCampaignEnd().toDate()));
    }

    public void stop(String messageKey) {
        for (T message : campaign.messages()) {
            if (message.messageKey().equals(messageKey)) {
                schedulerService.safeUnscheduleJob(getCampaignMessageSubject(message), getMessageJobId(messageKey));
            }
        }
    }

    public void stop() {
        for (T message : campaign.messages()) {
            stop(message.messageKey());
        }
        campaignEnrollmentService.unregister(campaignRequest.externalId(), campaignRequest.campaignName());
        schedulerService.safeUnscheduleJob(EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT, jobIdFactory.getCampaignCompletedJobIdFor(campaign.name(), campaignRequest.externalId()));
    }

    public Map<String, List<Date>> getCampaignTimings(Date startDate, Date endDate) {
        Map<String, List<Date>> messageTimingsMap = new HashMap<>();

        for (T message : campaign.messages()) {
            messageTimingsMap.put(message.name(),
                    schedulerService.getScheduledJobTimingsWithPrefix(
                            getCampaignMessageSubject(message),
                            getMessageJobId(message.messageKey()), startDate, endDate));
        }

        return messageTimingsMap;
    }

    protected abstract void scheduleJobFor(T message);

    protected abstract DateTime getCampaignEnd();

    protected abstract String getCampaignMessageSubject(T message);

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

    protected HashMap<String, Object> jobParams(String messageKey) {
        String jobId = getMessageJobId(messageKey);
        return new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withMessageKey(messageKey)
                .withExternalId(campaignRequest.externalId())
                .payload();
    }

    protected String getMessageJobId(String messageKey) {
        return jobIdFactory.getMessageJobIdFor(campaign.name(), campaignRequest.externalId(), messageKey);
    }
}

