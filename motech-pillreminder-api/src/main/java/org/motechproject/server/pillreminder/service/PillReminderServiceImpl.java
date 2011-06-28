package org.motechproject.server.pillreminder.service;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PillReminderServiceImpl implements PillReminderService {

    private AllPillRegimens allPillRegimens;
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
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("DosageID", dosage.getId());
            params.put("JobID", UUID.randomUUID().toString());

            MotechEvent motechEvent = new MotechEvent("org.motechproject.server.pillreminder.scheduler-reminder", params);
            String cronJobExpression = new CronJobExpressionBuilder(dosage.getStartHour(), dosage.getStartMinute(), pillRegimen.getReminderRepeatWindowInHours(), pillRegimen.getReminderRepeatIntervalInMinutes()).build();
            CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, pillRegimenRequest.getStartDate(), pillRegimenRequest.getEndDate());

            schedulerService.scheduleJob(schedulableJob);
        }
    }
}
