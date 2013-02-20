package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatIntervalCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;

@Component
public class RepeatIntervalCampaignSchedulerService extends CampaignSchedulerService<RepeatIntervalCampaignMessage, RepeatIntervalCampaign> {

    private static final int MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;

    @Autowired
    public RepeatIntervalCampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage m) {
        RepeatIntervalCampaign campaign = (RepeatIntervalCampaign) getAllMessageCampaigns().getCampaign(enrollment.getCampaignName());
        RepeatIntervalCampaignMessage message = (RepeatIntervalCampaignMessage) m;
        MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
        DateTime start = newDateTime(enrollment.getReferenceDate(), deliverTimeFor(enrollment, message));
        DateTime end = start.plus(campaign.maxDuration());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob()
            .setMotechEvent(motechEvent)
            .setStartTime(start.toDate())
            .setEndTime(end.toDate())
            .setRepeatIntervalInMilliSeconds(message.getRepeatIntervalInMillis())
            .setIgnorePastFiresAtStart(true)
            .setUseOriginalFireTimeAfterMisfire(true);
        getSchedulerService().safeScheduleRepeatingJob(job);
    }

    @Override
    protected Time deliverTimeFor(CampaignEnrollment enrollment, CampaignMessage message) {
        RepeatIntervalCampaignMessage repeatIntervalCampaignMessage = (RepeatIntervalCampaignMessage) message;
        if (repeatIntervalCampaignMessage.getRepeatIntervalInMillis() < MILLIS_IN_A_DAY) {
            return enrollment.getReferenceTime();
        }
        return enrollment.getDeliverTime() != null ? enrollment.getDeliverTime() : message.getStartTime();
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        RepeatIntervalCampaign campaign = (RepeatIntervalCampaign) getAllMessageCampaigns().getCampaign(enrollment.getCampaignName());
        for (RepeatIntervalCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleRepeatingJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }
}
