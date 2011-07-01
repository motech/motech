package org.motechproject.server.pillreminder.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.service.PillReminderServiceImpl;
import org.motechproject.server.pillreminder.util.TestUtil;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testPillReminder.xml"})
public class PillReminderServiceIT {

    @Autowired
    private AllPillRegimens allPillRegimens;
    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Date startDate;
    private Date endDate;
    private PillReminderServiceImpl pillReminderService;

    @Before
    public void setUp() {
        startDate = TestUtil.newDate(2011, 01, 20);
        endDate = TestUtil.newDate(2012, 01, 20);
        pillReminderService = new PillReminderServiceImpl(allPillRegimens, schedulerService);
    }

    @Test
    public void shouldSaveThePillRegimenAndScheduleJob() throws SchedulerException {

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;
        ArrayList<DosageRequest> dosageContracts = new ArrayList<DosageRequest>();
        dosageContracts.add(new DosageRequest(9, 05, new ArrayList<String>()));
        dosageContracts.add(new DosageRequest(21, 05, new ArrayList<String>()));
        pillReminderService.createNew(new PillRegimenRequest("1234", startDate, endDate, 2, 15, dosageContracts));
        assertEquals(scheduledJobsNum+2, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);

    }}
