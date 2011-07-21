package org.motechproject.server.pillreminder.service;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.springframework.beans.factory.annotation.Autowired;

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
            Map<String, Object> params = new SchedulerPayloadBuilder().
                    withJobId(dosage.getId()).
                    withDosageId(dosage.getId()).
                    withExternalId(pillRegimen.getExternalId()).payload();

            MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT, params);
            String cronJobExpression = new CronJobExpressionBuilder(dosage.getStartTime(), pillRegimen.getReminderRepeatWindowInHours(), pillRegimen.getReminderRepeatIntervalInMinutes()).build();
            CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, dosage.getStartDate(), dosage.getEndDate());

            schedulerService.scheduleJob(schedulableJob);
        }
    }

    @Override
    public void renew(PillRegimenRequest newScheduleRequest) {
        destroy(newScheduleRequest.getExternalId());
        createNew(newScheduleRequest);
    }

    private void destroy(String externalID) {
       PillRegimen regimen = allPillRegimens.findByExternalId(externalID);
       for(Dosage dosage : regimen.getDosages()) {
           schedulerService.unscheduleJob(dosage.getId());
       }
        allPillRegimens.remove(regimen);
    }
}
