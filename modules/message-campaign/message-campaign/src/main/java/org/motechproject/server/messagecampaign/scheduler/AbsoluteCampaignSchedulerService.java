package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;

@Component
public class AbsoluteCampaignSchedulerService extends CampaignSchedulerService<AbsoluteCampaignMessage, AbsoluteCampaign> {

    private CampaignEnrollmentService campaignEnrollmentService;

    @Autowired
    public AbsoluteCampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage campaignMessage) {
        Map<String, Object> params = jobParams(campaignMessage.messageKey(), enrollment);
        MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, params);
        LocalDate startDate = ((AbsoluteCampaignMessage) campaignMessage).date();
        RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, newDateTime(startDate, deliverTimeFor(enrollment, campaignMessage)).toDate());
        getSchedulerService().scheduleRunOnceJob(runOnceSchedulableJob);
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        AbsoluteCampaign campaign = (AbsoluteCampaign) getAllMessageCampaigns().get(enrollment.getCampaignName());
        for (AbsoluteCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleRunOnceJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }
}
