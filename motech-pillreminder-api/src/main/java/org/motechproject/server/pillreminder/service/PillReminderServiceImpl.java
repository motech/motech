package org.motechproject.server.pillreminder.service;

import org.apache.log4j.Logger;
import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.event.annotations.MotechListenerType;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class PillReminderServiceImpl implements PillReminderService {

    @Autowired
    private AllPillRegimens allPillRegimens;
    @Autowired
    private MotechSchedulerService schedulerService;

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

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
            Map<String, Object> params = new SchedulerPayloadBuilder()
                    .withJobId(dosage.getId())
                    .withDosageId(dosage.getId())
                    .withPillRegimenId(pillRegimen.getId())
                    .withExternalId(pillRegimen.getExternalId()).payload();

            MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT, params);
            String cronJobExpression = new CronJobExpressionBuilder(
                    dosage.getStartTime(),
                    pillRegimen.getReminderRepeatWindowInHours(),
                    pillRegimen.getReminderRepeatIntervalInMinutes()).build();

            CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, dosage.getStartDate(), dosage.getEndDate());
            schedulerService.scheduleJob(schedulableJob);
        }
    }

    @MotechListener(subjects = {"org.motechproject.server.pillreminder.scheduler-reminder"})
    public void handlePillReminderEvent(MotechEvent motechEvent) {
    }


    @Override
    public void renew(PillRegimenRequest newScheduleRequest) {
        destroy(newScheduleRequest.getExternalId());
        createNew(newScheduleRequest);
    }

    @Override
    public List<String> medicinesFor(String pillRegimenId, String dosageId) {
        return allPillRegimens.medicinesFor(pillRegimenId, dosageId);
    }

    private void destroy(String externalID) {
        PillRegimen regimen = allPillRegimens.findByExternalId(externalID);
        for (Dosage dosage : regimen.getDosages()) {
            schedulerService.unscheduleJob(dosage.getId());
        }
        allPillRegimens.remove(regimen);
    }
}
