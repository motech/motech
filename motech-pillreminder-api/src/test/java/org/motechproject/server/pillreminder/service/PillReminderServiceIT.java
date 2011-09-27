package org.motechproject.server.pillreminder.service;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.DayOfTheWeek;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.WeeklyPillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.util.DateUtil;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationPillReminderAPI.xml"})
public class PillReminderServiceIT {

    @Autowired
    private org.motechproject.server.pillreminder.service.PillReminderService pillReminderService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    
    @Autowired
    private AllPillRegimens allPillRegimens;

    private LocalDate startDate;
    private LocalDate endDate;

    @Before
    public void setUp() {
        startDate = DateUtil.newDate(2011, 01, 20);
        endDate = DateUtil.newDate(2012, 01, 20);
    }

    @Test
    public void shouldSaveTheDailyPillRegimenAndScheduleJob() throws SchedulerException {

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        ArrayList<MedicineRequest> medicineRequests = new ArrayList<MedicineRequest>();
        MedicineRequest medicineRequest1 = new MedicineRequest("m1", startDate, endDate);
        medicineRequests.add(medicineRequest1);
        MedicineRequest medicineRequest2 = new MedicineRequest("m2", startDate, startDate.plusDays(5));
        medicineRequests.add(medicineRequest2);

        ArrayList<DosageRequest> dosageContracts = new ArrayList<DosageRequest>();
        dosageContracts.add(new DosageRequest(9, 5, medicineRequests));

        pillReminderService.createNew(new DailyPillRegimenRequest("1234", 2, 15, dosageContracts));
        Assert.assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void shouldRenewThePillRegimenAndScheduleJob() throws SchedulerException {

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        ArrayList<MedicineRequest> medicineRequests = new ArrayList<MedicineRequest>();
        MedicineRequest medicineRequest1 = new MedicineRequest("m1", startDate, endDate);
        medicineRequests.add(medicineRequest1);
        MedicineRequest medicineRequest2 = new MedicineRequest("m2", startDate, startDate.plusDays(5));
        medicineRequests.add(medicineRequest2);

        ArrayList<DosageRequest> dosageContracts = new ArrayList<DosageRequest>();
        dosageContracts.add(new DosageRequest(9, 5, medicineRequests));

        String externalId = "123456789";
		pillReminderService.createNew(new DailyPillRegimenRequest(externalId, 2, 15, dosageContracts));

        ArrayList<DosageRequest> newDosageContracts = new ArrayList<DosageRequest>();
        newDosageContracts.add(new DosageRequest(9, 5, Arrays.asList(new MedicineRequest("m1", DateUtil.today(), DateUtil.today().plusDays(100)))));
        newDosageContracts.add(new DosageRequest(4, 5, Arrays.asList(new MedicineRequest("m2", DateUtil.today(), DateUtil.today().plusDays(100)))));
        pillReminderService.renew(new DailyPillRegimenRequest(externalId, 2, 15, newDosageContracts));
        Assert.assertEquals(scheduledJobsNum + 2, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
        PillRegimen regimen = allPillRegimens.findByExternalId(externalId);
        allPillRegimens.remove(regimen);
    }
}
