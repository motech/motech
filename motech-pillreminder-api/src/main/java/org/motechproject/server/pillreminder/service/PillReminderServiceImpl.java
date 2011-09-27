package org.motechproject.server.pillreminder.service;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobSimpleExpressionBuilder;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.contract.WeeklyPillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

public class PillReminderServiceImpl implements PillReminderService {
    @Autowired
    private AllPillRegimens allPillRegimens;
    @Autowired
    private MotechSchedulerService schedulerService;

    public PillReminderServiceImpl(AllPillRegimens allPillRegimens, MotechSchedulerService schedulerService) {
        this.allPillRegimens = allPillRegimens;
        this.schedulerService = schedulerService;
    }

    @Override
    public void createNew(DailyPillRegimenRequest dailyPillRegimenRequest) {
        PillRegimenBuilder builder = new PillRegimenBuilder();
        PillRegimen pillRegimen = builder.createFrom(dailyPillRegimenRequest);
        pillRegimen.validate();
        allPillRegimens.add(pillRegimen);

        for (Dosage dosage : pillRegimen.getDosages()) {
            Map<String, Object> eventParams = new SchedulerPayloadBuilder()
                    .withJobId(dosage.getId())
                    .withDosageId(dosage.getId())
                    .withPillRegimenId(pillRegimen.getId())
                    .withExternalId(pillRegimen.getExternalId()).payload();

                MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER, eventParams);
                String cronJobExpression = new CronJobSimpleExpressionBuilder(dosage.getDosageTime()).build();
                Date endDate = dosage.getEndDate() == null ? null : dosage.getEndDate().toDate();
                CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, dosage.getStartDate().toDate(), endDate);
                schedulerService.scheduleJob(schedulableJob);
        }
    }

    @Override
    public void createNew(WeeklyPillRegimenRequest weeklyPillRegimenRequest) {

    }

    @Override
    public void renew(DailyPillRegimenRequest newDailyScheduleRequest) {
        destroy(newDailyScheduleRequest.getExternalId());
        createNew(newDailyScheduleRequest);
    }

    @Override
    public void dosageStatusKnown(String pillRegimenId, String dosageId, LocalDate lastCapturedDate) {
        allPillRegimens.updateLastCapturedDate(pillRegimenId, dosageId, lastCapturedDate);
    }

    @Override
    public PillRegimenResponse getPillRegimen(String externalId) {
        PillRegimen pillRegimen = allPillRegimens.findByExternalId(externalId);
        return pillRegimen == null ? null : new PillRegimenResponseBuilder().createFrom(pillRegimen);
    }

    private void destroy(String externalID) {
        PillRegimen regimen = allPillRegimens.findByExternalId(externalID);
        for (Dosage dosage : regimen.getDosages())
            schedulerService.unscheduleJob(dosage.getId());
        allPillRegimens.remove(regimen);
    }
}
