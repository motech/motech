package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

public class OffsetProgramScheduler extends MessageCampaignScheduler<OffsetCampaignMessage, OffsetCampaign> {

    public OffsetProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, OffsetCampaign campaign, CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, enrollRequest, campaign, campaignEnrollmentService);
    }

    @Override
    protected void scheduleJobFor(OffsetCampaignMessage offsetCampaignMessage) {
        Time reminderTime = campaignRequest.reminderTime();
        LocalDate jobDate = jobDate(referenceDate(), offsetCampaignMessage.timeOffset());

        scheduleJobOn(reminderTime, jobDate, jobParams(offsetCampaignMessage.messageKey()));
    }

    private LocalDate jobDate(LocalDate referenceDate, String timeOffset) {
        WallTime wallTime = WallTimeFactory.wallTime(timeOffset);
        int offSetDays = wallTime.inDays();
        return referenceDate.plusDays(offSetDays);
    }
}
