package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollForRelativeProgramRequest;
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
    public void scheduleJob(String campaignName, CampaignMessage message) {
        String jobId = campaignName + "_" + message.name() + "_" + enrollRequest.externalId();

        Date referenceDate = enrollRequest.referenceDate();
        Date jobDate = jobDate(referenceDate, message.timeOffset());

        HashMap params = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaignName)
                .withExternalId(enrollRequest.externalId())
                .payload();

        scheduleJob(jobDate, enrollRequest.reminderTime(), params);
    }

    private Date jobDate(Date referenceDate, String timeOffset) {
        WallTime wallTime = WallTimeFactory.create(timeOffset);
        int offSetDays = wallTime.inDays();
        return new DateTime(referenceDate).plusDays(offSetDays).toDate();
    }
}
