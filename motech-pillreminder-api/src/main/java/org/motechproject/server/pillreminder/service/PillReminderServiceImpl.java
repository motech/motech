package org.motechproject.server.pillreminder.service;

import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.builder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;

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
        MotechEvent someevent = new MotechEvent("someevent");
        someevent.getParameters().put(MotechSchedulerService.JOB_ID_KEY, pillRegimenRequest.getExternalId());
        String cronExpression = "0 15 13 * * ? *";
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(someevent, cronExpression, pillRegimen.getStartDate(), pillRegimen.getEndDate());
        schedulerService.scheduleJob(cronSchedulableJob);
    }

}
