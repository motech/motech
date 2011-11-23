package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode;
import org.motechproject.valueobjects.WallTime;

import java.util.Map;

import static java.lang.String.format;
import static org.motechproject.valueobjects.factory.WallTimeFactory.create;

public class RepeatingProgramScheduler extends MessageCampaignScheduler<RepeatingCampaignMessage, RepeatingCampaign> {

    public static final int DEFAULT_INTERVAL_OFFSET = 1;

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, RepeatingCampaign campaign) {
        super(schedulerService, enrollRequest, campaign);
    }

    @Override
    protected void scheduleJobFor(RepeatingCampaignMessage message) {
        WallTime maxDuration = create(campaign.maxDuration());
        LocalDate startDate = referenceDate();
        LocalDate endDate = startDate.plusDays(message.duration(maxDuration,campaignRequest));

        if(startDate.compareTo(endDate) > 0) throw new IllegalArgumentException(format("startDate (%s) is after endDate (%s) for - (%s)", startDate, endDate, campaignRequest));
        
        long repeatInterval = Days.days(message.repeatIntervalForSchedule()).toStandardSeconds().getSeconds() * 1000L;
        Map<String, Object> params = jobParams(message.messageKey());
        params.put(EventKeys.REPEATING_START_OFFSET, startOffset(message));
        scheduleRepeatingJob(startDate, endDate, repeatInterval, params);
    }

    private int startOffset(RepeatingCampaignMessage message) {
        Integer offset = campaignRequest.startOffset();
        return isRepeatingIntervalMode(message) ? DEFAULT_INTERVAL_OFFSET : (offset == null ? DEFAULT_INTERVAL_OFFSET : offset);
    }

    private boolean isRepeatingIntervalMode(RepeatingCampaignMessage message) {
        return message.mode().equals(RepeatingMessageMode.REPEAT_INTERVAL);
    }
}
