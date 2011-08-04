package org.motechproject.server.pillreminder.service;

import org.joda.time.DateTime;
import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.builder.DosageResponseBuilder;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.Util;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
    public void createNew(PillRegimenRequest pillRegimenRequest) {
        PillRegimenBuilder builder = new PillRegimenBuilder();
        PillRegimen pillRegimen = builder.createFrom(pillRegimenRequest);
        pillRegimen.validate();
        allPillRegimens.add(pillRegimen);

        for (Dosage dosage : pillRegimen.getDosages()) {
            Map<String, Object> eventParams = new SchedulerPayloadBuilder()
                    .withJobId(dosage.getId())
                    .withDosageId(dosage.getId())
                    .withPillRegimenId(pillRegimen.getId())
                    .withExternalId(pillRegimen.getExternalId()).payload();

            MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER, eventParams);
            String cronJobExpression = new CronJobExpressionBuilder(
                    dosage.getDosageTime(),
                    pillRegimen.getReminderRepeatWindowInHours(),
                    pillRegimen.getReminderRepeatIntervalInMinutes()).build();

            CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, dosage.getStartDate(), dosage.getEndDate());
            schedulerService.scheduleJob(schedulableJob);
        }
    }

    @Override
    public void renew(PillRegimenRequest newScheduleRequest) {
        destroy(newScheduleRequest.getExternalId());
        createNew(newScheduleRequest);
    }

    @Override
    public void stopTodaysReminders(String pillRegimenId, String dosageId) {
        allPillRegimens.stopTodaysReminders(pillRegimenId, dosageId);
    }

    @Override
    public PillRegimenResponse getPillRegimen(String pillRegimenId) {
        PillRegimen pillRegimen = allPillRegimens.get(pillRegimenId);
        return pillRegimen == null ? null : new PillRegimenResponseBuilder().createFrom(pillRegimen);
    }

    public List<String> medicinesFor(String pillRegimenId, String dosageId) {
        return allPillRegimens.medicinesFor(pillRegimenId, dosageId);
    }

    public DosageResponse getPreviousDosage(String pillRegimenId, String currentDosageId) {
        PillRegimen pillRegimen = allPillRegimens.get(pillRegimenId);
        Dosage currentDosage = pillRegimen.getDosage(currentDosageId);
        Dosage previousDosage = pillRegimen.getPreviousDosage(currentDosage);
        return new DosageResponseBuilder().createFrom(previousDosage);
    }

    public DateTime getNextDosageTime(String pillRegimenId, String currentDosageId) {
        PillRegimen pillRegimen = allPillRegimens.get(pillRegimenId);
        Dosage currentDosage = pillRegimen.getDosage(currentDosageId);
        Dosage nextDosage = pillRegimen.getNextDosage(currentDosage);
        return nextDosage == null ? null : nextDosage.getDosageTime().getDateTime(Util.currentDateTime());
    }

    private void destroy(String externalID) {
        PillRegimen regimen = allPillRegimens.findByExternalId(externalID);
        for (Dosage dosage : regimen.getDosages())
            schedulerService.unscheduleJob(dosage.getId());
        allPillRegimens.remove(regimen);
    }
}
