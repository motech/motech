package org.motechproject.server.pillreminder.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.util.Util;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.motechproject.server.pillreminder.util.Util.getDateAfter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationPillReminderAPI.xml"})
public class PillReminderServiceIT {

    @Autowired
    private org.motechproject.server.pillreminder.service.PillReminderService pillReminderService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() {
        startDate = Util.newDate(2011, 01, 20);
        endDate = Util.newDate(2012, 01, 20);
    }

    @Test
    public void shouldSaveThePillRegimenAndScheduleJob() throws SchedulerException {

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        ArrayList<MedicineRequest> medicineRequests = new ArrayList<MedicineRequest>();
        MedicineRequest medicineRequest1 = new MedicineRequest("m1", startDate, endDate);
        medicineRequests.add(medicineRequest1);
        MedicineRequest medicineRequest2 = new MedicineRequest("m2", startDate, getDateAfter(startDate, 5));
        medicineRequests.add(medicineRequest2);

        ArrayList<DosageRequest> dosageContracts = new ArrayList<DosageRequest>();
        dosageContracts.add(new DosageRequest(9, 5, medicineRequests));

        pillReminderService.createNew(new PillRegimenRequest("1234", 2, 15, dosageContracts));
        Assert.assertEquals(scheduledJobsNum + 1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }
}
