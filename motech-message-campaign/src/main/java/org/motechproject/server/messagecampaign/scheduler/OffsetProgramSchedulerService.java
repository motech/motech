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
        int interval = offsetMessage.timeOffset().toStandardDays().getDays();

        LocalDate jobDate = enrollment.getReferenceDate().plusDays(interval);
        if (isInFuture(jobDate, reminderTime)) {
            MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
            Date startDateTime = newDateTime(jobDate, reminderTime).toDate();
            RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDateTime);
            getSchedulerService().scheduleRunOnceJob(runOnceSchedulableJob);
        }
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        OffsetCampaign campaign = (OffsetCampaign) getAllMessageCampaigns().get(enrollment.getCampaignName());
        for (OffsetCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleRunOnceJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }

    private boolean isInFuture(LocalDate date, Time time) {
        return DateUtil.newDateTime(date, time).isAfter(DateUtil.now());
    }
}

