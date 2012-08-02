package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CampaignSchedulerService<MESSAGE extends CampaignMessage, CAMPAIGN extends Campaign<MESSAGE>> {

    private MotechSchedulerService schedulerService;
    private AllMessageCampaigns allMessageCampaigns;
    private JobIdFactory jobIdFactory;

    protected CampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        this.schedulerService = schedulerService;
        this.allMessageCampaigns = allMessageCampaigns;
        jobIdFactory = new JobIdFactory();
    }

    public void start(CampaignEnrollment enrollment) {
        CAMPAIGN campaign = (CAMPAIGN) allMessageCampaigns.get(enrollment.getCampaignName());
        for (MESSAGE message : campaign.getMessages()) {
            scheduleMessageJob(enrollment, message);
        }
    }

    public void stop(CampaignEnrollment enrollment) {
        CAMPAIGN campaign = (CAMPAIGN) allMessageCampaigns.get(enrollment.getCampaignName());
        for (MESSAGE message : campaign.getMessages()) {
            schedulerService.safeUnscheduleJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }

    public Map<String, List<Date>> getCampaignTimings(Date startDate, Date endDate, CampaignEnrollment enrollment) {
        Map<String, List<Date>> messageTimingsMap = new HashMap<>();
        CAMPAIGN campaign = (CAMPAIGN) allMessageCampaigns.get(enrollment.getCampaignName());
        for (MESSAGE message : campaign.getMessages()) {
            messageTimingsMap.put(message.name(),
            schedulerService.getScheduledJobTimingsWithPrefix(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()), startDate, endDate));
        }
        return messageTimingsMap;
    }

    protected abstract void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage message);

    protected Time deliverTimeFor(CampaignEnrollment enrollment, CampaignMessage message) {
        return enrollment.getDeliverTime() != null ? enrollment.getDeliverTime() : message.getStartTime();
    }

    protected HashMap<String, Object> jobParams(String messageKey, CampaignEnrollment enrollment) {
        Campaign campaign = allMessageCampaigns.get(enrollment.getCampaignName());
        return new SchedulerPayloadBuilder()
            .withJobId(messageJobIdFor(messageKey, enrollment.getExternalId(), enrollment.getCampaignName()))
            .withCampaignName(campaign.getName())
            .withMessageKey(messageKey)
            .withExternalId(enrollment.getExternalId())
            .payload();
    }

    protected String messageJobIdFor(String messageKey, String externalId, String campaignName) {
        return jobIdFactory.getMessageJobIdFor(campaignName, externalId, messageKey);
    }

    public MotechSchedulerService getSchedulerService() {
        return schedulerService;
    }

    public AllMessageCampaigns getAllMessageCampaigns() {
        return allMessageCampaigns;
    }
}

