package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.max;
import static org.motechproject.util.DateUtil.newDateTime;

public class OffsetProgramScheduler extends MessageCampaignScheduler<OffsetCampaignMessage, OffsetCampaign> {

    public OffsetProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, OffsetCampaign campaign, CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, enrollRequest, campaign, campaignEnrollmentService);
    }

    @Override
    protected void scheduleJobFor(OffsetCampaignMessage offsetCampaignMessage) {
        Time reminderTime = campaignRequest.reminderTime();
        int offset = offsetInDays(offsetCampaignMessage.timeOffset());
        LocalDate jobDate = referenceDate().plusDays(offset);

        scheduleJobOn(reminderTime, jobDate, jobParams(offsetCampaignMessage.messageKey()));
    }

    @Override
    protected DateTime getCampaignEnd() {
        List<Integer> timeOffsets = new ArrayList<Integer>();
        for(OffsetCampaignMessage message : campaign.messages())
            timeOffsets.add(offsetInDays(message.timeOffset()));

        LocalDate campaignEndDate = campaignRequest.referenceDate().plusDays(max(timeOffsets));
        return newDateTime(campaignEndDate,campaignRequest.reminderTime());
    }

    private int offsetInDays(String timeOffset) {
        WallTime wallTime = WallTimeFactory.wallTime(timeOffset);
        return wallTime.inDays();
    }
}
