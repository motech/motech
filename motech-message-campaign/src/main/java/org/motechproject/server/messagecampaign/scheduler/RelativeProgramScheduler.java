package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.CampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.Date;
import java.util.HashMap;

public class RelativeProgramScheduler extends MessageCampaignScheduler {

    private EnrollForRelativeProgramRequest enrollRequest;

    public RelativeProgramScheduler(MotechSchedulerService schedulerService, EnrollForRelativeProgramRequest enrollRequest) {
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    @Override
    public void scheduleJob(Campaign campaign, CampaignMessage message) {
        Date referenceDate = enrollRequest.referenceDate();
        Time reminderTime = enrollRequest.reminderTime();

        if (messageHasRepeatInterval(message)) {
            scheduleRepeatingJobs(message.repeatInterval(), campaign.maxDuration(), message, campaign.getName());
        } else {
            String jobId = campaign.getName() + "_" + message.name() + "_" + enrollRequest.externalId();

            HashMap jobParams = new SchedulerPayloadBuilder()
                    .withJobId(jobId)
                    .withCampaignName(campaign.getName())
                    .withExternalId(enrollRequest.externalId())
                    .payload();

            Date jobDate = jobDate(referenceDate, message.timeOffset());
            scheduleJobOn(reminderTime, jobDate, jobParams);
        }
    }

    private void scheduleRepeatingJobs(String repeatInterval, String maxDuration, CampaignMessage message, String campaignName) {

        DateTime startDate = new DateTime(enrollRequest.referenceDate());
        WallTime duration = WallTimeFactory.create(maxDuration);
        DateTime endDate = startDate.plusDays(duration.inDays());
        int repeatIntervalInDays = WallTimeFactory.create(repeatInterval).inDays();

        DateTime jobDate = startDate;
        while (jobDate.isBefore(endDate)) {
            String jobId = campaignName + "_" + message.name() + "_" + enrollRequest.externalId() + "_" + jobDate.toString("yyyy-mm-dd");
            HashMap jobParams = new SchedulerPayloadBuilder()
                    .withJobId(jobId)
                    .withCampaignName(campaignName)
                    .withExternalId(enrollRequest.externalId())
                    .payload();

            scheduleJobOn(enrollRequest.reminderTime(), jobDate.toDate(), jobParams);
            jobDate = jobDate.plusDays(repeatIntervalInDays);
        }
    }

    private boolean messageHasRepeatInterval(CampaignMessage message) {
        return message.repeatInterval() != null;
    }

    private Date jobDate(Date referenceDate, String timeOffset) {
        WallTime wallTime = WallTimeFactory.create(timeOffset);
        int offSetDays = wallTime.inDays();
        return new DateTime(referenceDate).plusDays(offSetDays).toDate();
    }
}
