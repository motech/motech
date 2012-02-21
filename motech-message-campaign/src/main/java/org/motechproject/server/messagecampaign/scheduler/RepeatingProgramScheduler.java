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
import org.motechproject.valueobjects.WallTime;

import java.util.Date;
import java.util.Map;

import static java.lang.String.format;
import static org.motechproject.util.DateUtil.endOfDay;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.valueobjects.factory.WallTimeFactory.wallTime;

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
        WallTime maxDuration = wallTime(campaign.maxDuration());
        LocalDate startDate = referenceDate();
        LocalDate endDate = startDate.plusDays(message.durationInDaysToAdd(maxDuration, campaignRequest));
        Date endDateToEndOfDay = endOfDay(endDate.toDate()).toDate();

        if (startDate.toDate().compareTo(endDateToEndOfDay) > 0) throw new IllegalArgumentException(format("startDate (%s) is after endDate (%s) for - (%s)", startDate, endDateToEndOfDay, campaignRequest));

        scheduleRepeatingJob(startDate, campaignRequest.reminderTime(), endDateToEndOfDay, jobParams(message.messageKey()));
    }

    private void scheduleRepeatingJob(LocalDate startDate, Time reminderTime, Date endDate, Map<String, Object> params) {
        MotechEvent motechEvent = new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, params);
        Date startDateAsDate = startDate == null ? null : newDateTime(startDate, reminderTime).withMillisOfSecond(0).toDate();
        Date endDateAsDate = endDate == null ? null : endDate;
        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronExpressionFor(reminderTime), startDateAsDate, endDateAsDate);
        schedulerService.safeScheduleJob(schedulableJob);
    }

    private String cronExpressionFor(Time reminderTime) {
        return String.format("0 %d %d 1/1 * ? *", reminderTime.getMinute(), reminderTime.getHour());
    }


}
