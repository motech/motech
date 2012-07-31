package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatIntervalCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class RepeatIntervalCampaignSchedulerService extends CampaignSchedulerService<RepeatIntervalCampaignMessage, RepeatIntervalCampaign> {

    @Autowired
    public RepeatIntervalCampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage m) {
        RepeatIntervalCampaign campaign = (RepeatIntervalCampaign) allMessageCampaigns.get(enrollment.getCampaignName());
        RepeatIntervalCampaignMessage message = (RepeatIntervalCampaignMessage) m;
        MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
        DateTime start = newDateTime(enrollment.getReferenceDate(), deliverTimeFor(enrollment, message));
        DateTime end = start.plus(campaign.maxDuration());
        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(motechEvent, start.toDate(), end.toDate(), message.getRepeatIntervalInMillis(), true));
    }
}
