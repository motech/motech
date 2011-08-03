package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.SchedulerPayloadBuilder;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.factory.WallTimeFactory;

import java.util.Date;
import java.util.HashMap;

public class OffsetProgramScheduler extends MessageCampaignScheduler {

    private EnrollRequest enrollRequest;
    private OffsetCampaign campaign;

    public OffsetProgramScheduler(MotechSchedulerService schedulerService, EnrollRequest enrollRequest, OffsetCampaign campaign) {
        this.campaign = campaign;
        this.schedulerService = schedulerService;
        this.enrollRequest = enrollRequest;
    }

    private void scheduleJob(OffsetCampaignMessage message) {
        Date referenceDate = enrollRequest.referenceDate();
        Time reminderTime = enrollRequest.reminderTime();

        String jobId = EventKeys.BASE_SUBJECT + campaign.name() + "." + message.name() + "." + enrollRequest.externalId();

        HashMap jobParams = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withCampaignName(campaign.name())
                .withExternalId(enrollRequest.externalId())
                .withMessageKey(message.messageKey())
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
