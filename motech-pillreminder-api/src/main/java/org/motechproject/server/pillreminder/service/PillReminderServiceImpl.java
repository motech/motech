package org.motechproject.server.pillreminder.service;

import org.motechproject.builder.CronJobExpressionBuilder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.*;

public class PillReminderServiceImpl implements PillReminderService {

    public static final int DEFAULT_REGIMEN_PERIOD_IN_MONTHS = 12;
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
            for (Medicine medicine : dosage.getMedicines()) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(EventKeys.DOSAGE_ID_KEY, dosage.getId());
                params.put(EventKeys.SCHEDULE_JOB_ID_KEY, UUID.randomUUID().toString());

                MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT, params);
                String cronJobExpression = new CronJobExpressionBuilder(dosage.getStartHour(), dosage.getStartMinute(), pillRegimen.getReminderRepeatWindowInHours(), pillRegimen.getReminderRepeatIntervalInMinutes()).build();
                CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, medicine.getStartDate(), getReminderEndDate(medicine));

                schedulerService.scheduleJob(schedulableJob);
            }
        }
    }

    private Date getReminderEndDate(Medicine medicine) {
        if(medicine.getEndDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(medicine.getStartDate());
            calendar.add(Calendar.MONTH, DEFAULT_REGIMEN_PERIOD_IN_MONTHS);
            return calendar.getTime();
        }
        return medicine.getEndDate();
    }
}
