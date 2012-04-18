package org.motechproject.server.pillreminder.api.service;

import org.joda.time.DateTime;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobSimpleExpressionBuilder;
import org.motechproject.server.pillreminder.api.EventKeys;
import org.motechproject.server.pillreminder.api.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.api.domain.Dosage;
import org.motechproject.server.pillreminder.api.domain.PillRegimen;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class PillRegimenJobScheduler {
    private MotechSchedulerService schedulerService;

    @Autowired
    public PillRegimenJobScheduler(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void scheduleDailyJob(PillRegimen pillRegimen) {
        for (Dosage dosage : pillRegimen.getDosages()) {
            CronSchedulableJob schedulableJob = getSchedulableDailyJob(pillRegimen, dosage);
            schedulerService.safeScheduleJob(schedulableJob);
        }
    }

    public void unscheduleJobs(PillRegimen regimen) {
        for (Dosage dosage : regimen.getDosages()) {
            schedulerService.safeUnscheduleJob(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER, dosage.getId());
            schedulerService.safeUnscheduleRepeatingJob(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER, dosage.getId());
        }
    }

    protected CronSchedulableJob getSchedulableDailyJob(PillRegimen pillRegimen, Dosage dosage) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder()
                .withJobId(dosage.getId())
                .withDosageId(dosage.getId())
                .withExternalId(pillRegimen.getExternalId())
                .payload();

        final Time dosageTime = dosage.getDosageTime();
        DateTime cronStartDateTime = DateUtil.newDateTime(dosage.getStartDate(), dosageTime.getHour(), dosageTime.getMinute(), 0);
        DateTime adjustedCronStartDateTime = cronStartDateTime.plusMinutes(pillRegimen.getScheduleDetails().getBufferOverDosageTimeInMinutes());

        MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER, eventParams);
        String cronJobExpression = new CronJobSimpleExpressionBuilder(new Time(adjustedCronStartDateTime.toLocalTime())).build();
        Date endDate = dosage.getEndDate() == null ? null : dosage.getEndDate().toDate();
        Date startDate = DateUtil.newDateTime(adjustedCronStartDateTime.toDate()).isBefore(DateUtil.now()) ? DateUtil.now().toDate() : adjustedCronStartDateTime.toDate();
        return new CronSchedulableJob(motechEvent, cronJobExpression, startDate, endDate);
    }
}