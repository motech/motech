package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class OffsetProgramSchedulerService extends CampaignSchedulerService<OffsetCampaignMessage, OffsetCampaign> {

    @Autowired
    public OffsetProgramSchedulerService(MotechSchedulerService schedulerService,
            AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage message) {
        Time reminderTime = deliverTimeFor(enrollment, message);
        OffsetCampaignMessage offsetMessage = (OffsetCampaignMessage) message;
        Minutes offsetMinutes = offsetMessage.timeOffset().toStandardMinutes();
        Days offsetDays = offsetMessage.timeOffset().toStandardDays();
        LocalDate jobDate = enrollment.getReferenceDate().plusDays(offsetDays.getDays());
        if (offsetMinutes.toStandardDays().getDays() > 0) {
            if (isInFuture(jobDate, reminderTime)) {
                MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(),
                        enrollment));
                Date startDateTime = newDateTime(jobDate, reminderTime).toDate();
                RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDateTime);
                getSchedulerService().scheduleRunOnceJob(runOnceSchedulableJob);
            }
        } else {
            Integer hour = reminderTime.getHour();
            Integer minutes = reminderTime.getMinute();
            LocalTime localTime = new LocalTime(hour, minutes);
            LocalTime localDeliveryTime = localTime.plusMinutes(offsetMinutes.getMinutes());
            Time deliveryTime = new Time(localDeliveryTime.getHourOfDay(), localDeliveryTime.getMinuteOfHour());
            if (isInFuture(DateUtil.today(), deliveryTime)) {
                MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(),
                        enrollment));
                Date startDateTime = newDateTime(jobDate, deliveryTime).toDate();
                RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDateTime);
                getSchedulerService().scheduleRunOnceJob(runOnceSchedulableJob);
            }
        }
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        OffsetCampaign campaign = (OffsetCampaign) getAllMessageCampaigns().get(enrollment.getCampaignName());
        for (OffsetCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleRunOnceJob(EventKeys.SEND_MESSAGE,
                    messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }

    private boolean isInFuture(LocalDate date, Time time) {
        return DateUtil.newDateTime(date, time).isAfter(DateUtil.now());
    }
}
