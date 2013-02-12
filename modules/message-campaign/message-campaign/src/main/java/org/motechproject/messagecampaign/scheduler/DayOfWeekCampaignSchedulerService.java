package org.motechproject.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.event.MotechEvent;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.DayOfWeekCampaign;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.DayOfWeekSchedulableJob;
import org.motechproject.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.messagecampaign.domain.message.DayOfWeekCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DayOfWeekCampaignSchedulerService extends CampaignSchedulerService<DayOfWeekCampaignMessage, DayOfWeekCampaign> {

    @Autowired
    public DayOfWeekCampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage message) {
        DayOfWeekCampaign campaign = (DayOfWeekCampaign) getAllMessageCampaigns().getCampaign(enrollment.getCampaignName());
        MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
        LocalDate start = enrollment.getReferenceDate();
        LocalDate end = start.plus(campaign.maxDuration());
        DayOfWeekCampaignMessage dayOfWeekMessage = (DayOfWeekCampaignMessage) message;
        getSchedulerService().scheduleDayOfWeekJob(new DayOfWeekSchedulableJob(motechEvent, start, end, dayOfWeekMessage.getDaysOfWeek(), deliverTimeFor(enrollment, message), true));
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        DayOfWeekCampaign campaign = (DayOfWeekCampaign) getAllMessageCampaigns().getCampaign(enrollment.getCampaignName());
        for (DayOfWeekCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }
}
