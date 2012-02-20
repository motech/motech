package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatingCampaign;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;

import java.util.Date;
import java.util.Map;

import static java.lang.String.format;
import static org.motechproject.valueobjects.factory.WallTimeFactory.create;

public class RepeatingProgramScheduler extends MessageCampaignScheduler<RepeatingCampaignMessage, RepeatingCampaign> {

    private CampaignEnrollmentService campaignEnrollmentService;

    public RepeatingProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest enrollRequest, RepeatingCampaign campaign,  CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, enrollRequest, campaign);
        this.campaignEnrollmentService = campaignEnrollmentService;
    }

    @Override
    public void start() {
        CampaignEnrollment enrollment = new CampaignEnrollment(campaignRequest.externalId(), campaignRequest.campaignName())
                .setStartDate(referenceDate()).setStartOffset(campaignRequest.startOffset());
        campaignEnrollmentService.register(enrollment);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        campaignEnrollmentService.unregister(campaignRequest.externalId(), campaignRequest.campaignName());
    }

    @Override
    protected void scheduleJobFor(RepeatingCampaignMessage message) {
        WallTime maxDuration = create(campaign.maxDuration());
        LocalDate startDate = referenceDate();
        LocalDate endDate = startDate.plusDays(message.durationInDaysToAdd(maxDuration, campaignRequest));

        if (startDate.compareTo(endDate) > 0) throw new IllegalArgumentException(format("startDate (%s) is after endDate (%s) for - (%s)", startDate, endDate, campaignRequest));

        scheduleRepeatingJob(startDate, campaignRequest.reminderTime(), endDate, jobParams(message.messageKey()));
    }

    private void scheduleRepeatingJob(LocalDate startDate, Time reminderTime, LocalDate endDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, params);
        Date startDateAsDate = startDate == null ? null : DateUtil.newDateTime(startDate, reminderTime).withMillisOfSecond(0).toDate();
        Date endDateAsDate = endDate == null ? null : endDate.toDate();
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronExpressionFor(reminderTime), startDateAsDate, endDateAsDate);
        schedulerService.safeScheduleJob(schedulableJob);
    }

    private String cronExpressionFor(Time reminderTime) {
        return String.format("0 %d %d 1/1 * ? *", reminderTime.getMinute(), reminderTime.getHour());
    }


}
