package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.ArrayList;
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

    public abstract void stop(CampaignEnrollment enrollment);

    public Map<String, List<DateTime>> getCampaignTimings(DateTime startDate, DateTime endDate, CampaignEnrollment enrollment) {
        Map<String, List<DateTime>> messageTimingsMap = new HashMap<>();
        CAMPAIGN campaign = (CAMPAIGN) allMessageCampaigns.get(enrollment.getCampaignName());
        for (MESSAGE message : campaign.getMessages()) {
            String externalJobIdPrefix = messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName());
            List<DateTime> dates = convertToDateTimeList(schedulerService.getScheduledJobTimingsWithPrefix(EventKeys.SEND_MESSAGE, externalJobIdPrefix, startDate.toDate(), endDate.toDate()));

            messageTimingsMap.put(message.name(), dates);
        }
        return messageTimingsMap;
    }

    protected abstract void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage message);

    protected Time deliverTimeFor(CampaignEnrollment enrollment, CampaignMessage message) {
        return enrollment.getDeliverTime() != null ? enrollment.getDeliverTime() : message.getStartTime();
    }

    protected Map<String, Object> jobParams(String messageKey, CampaignEnrollment enrollment) {
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

    private List<DateTime> convertToDateTimeList(final List<Date> dates) {
        List<DateTime> list = new ArrayList<>(dates.size());

        for (Date date : dates) {
            list.add(new DateTime(date));
        }

        return list;
    }
}

