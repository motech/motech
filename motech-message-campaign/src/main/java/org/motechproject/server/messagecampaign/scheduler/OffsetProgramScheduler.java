package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

public class OffsetProgramScheduler extends MessageCampaignScheduler {

    public OffsetProgramScheduler(MotechSchedulerService schedulerService, EnrollRequest enrollRequest, OffsetCampaign campaign) {
        super(schedulerService, enrollRequest, campaign);
    }

    @Override
    protected void scheduleJob(CampaignMessage message) {
        Time reminderTime = enrollRequest.reminderTime();
        OffsetCampaignMessage offsetCampaignMessage = (OffsetCampaignMessage) message;
        LocalDate jobDate = jobDate(referenceDate(), offsetCampaignMessage.timeOffset());

        scheduleJobOn(reminderTime, jobDate, jobParams(message));
    }

    private LocalDate jobDate(LocalDate referenceDate, String timeOffset) {
        WallTime wallTime = WallTimeFactory.create(timeOffset);
        int offSetDays = wallTime.inDays();
        return referenceDate.plusDays(offSetDays);
    }
}
