package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.Date;
import java.util.HashMap;

public class OffsetProgramScheduler extends MessageCampaignScheduler {

    private EnrollForRelativeProgramRequest enrollRequest;
    private OffsetCampaign campaign;

    public OffsetProgramScheduler(MotechSchedulerService schedulerService, EnrollForRelativeProgramRequest enrollRequest, OffsetCampaign campaign) {
        this.campaign = campaign;
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    private void scheduleJob(OffsetCampaignMessage message) {
        Date referenceDate = enrollRequest.referenceDate();
        Time reminderTime = enrollRequest.reminderTime();

        String jobId = campaign.name() + "_" + message.name() + "_" + enrollRequest.externalId();

        HashMap jobParams = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withExternalId(enrollRequest.externalId())
                .payload();

        Date jobDate = jobDate(referenceDate, message.timeOffset());
        scheduleJobOn(reminderTime, jobDate, jobParams);
    }

    private Date jobDate(Date referenceDate, String timeOffset) {
        WallTime wallTime = WallTimeFactory.create(timeOffset);
        int offSetDays = wallTime.inDays();
        return new DateTime(referenceDate).plusDays(offSetDays).toDate();
    }

    @Override
    public void scheduleJobs() {
        for (OffsetCampaignMessage message : campaign.messages()) {
            scheduleJob(message);
        }
    }

}
