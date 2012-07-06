package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class OffsetProgramSchedulerService extends CampaignSchedulerService<OffsetCampaignMessage, OffsetCampaign> {

    @Autowired
    public OffsetProgramSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage message) {
        Time reminderTime = deliverTimeFor(enrollment, message);
        OffsetCampaignMessage offsetMessage = (OffsetCampaignMessage) message;
        int interval = offsetInDays(offsetMessage.timeOffset());

        LocalDate jobDate = enrollment.getReferenceDate().plusDays(interval);
        if (isInFuture(jobDate, reminderTime)) {
            MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
            Date startDateTime = newDateTime(jobDate, reminderTime).toDate();
            RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDateTime);
            schedulerService.scheduleRunOnceJob(runOnceSchedulableJob);
        }
    }

    private boolean isInFuture(LocalDate date, Time time) {
        return DateUtil.newDateTime(date, time).isAfter(DateUtil.now());
    }

    // TODO: get rid of walltime
    private int offsetInDays(String timeOffset) {
        WallTime wallTime = new WallTime(timeOffset);
        return wallTime.inDays();
    }
}
