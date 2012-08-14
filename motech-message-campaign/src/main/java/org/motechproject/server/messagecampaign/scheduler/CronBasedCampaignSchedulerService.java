package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CronBasedCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CronBasedCampaignSchedulerService extends CampaignSchedulerService<CronBasedCampaignMessage, CronBasedCampaign> {

    @Autowired
    public CronBasedCampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage message) {
        CronBasedCampaignMessage cronMessage = (CronBasedCampaignMessage) message;
        MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
        LocalDate startDate = enrollment.getReferenceDate();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronMessage.cron(), startDate.toDate(), null);
        getSchedulerService().scheduleJob(schedulableJob);
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        CronBasedCampaign campaign = (CronBasedCampaign) getAllMessageCampaigns().get(enrollment.getCampaignName());
        for (CronBasedCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }
}
