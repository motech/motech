package org.motechproject.scheduler.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.web.domain.JobsRecords;
import org.quartz.SchedulerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class JobsControllerTest {

    private static final String DEFAULT_GROUP = "default-group";

    @InjectMocks
    JobsController jobsController = new JobsController();

    @Mock
    MotechSchedulerService motechSchedulerService;

    @Mock
    MotechSchedulerDatabaseService motechSchedulerDatabaseService;

    JobBasicInfo testJobBasicInfo1;
    JobBasicInfo testJobBasicInfo2;
    JobBasicInfo testJobBasicInfo3;
    JobBasicInfo testJobBasicInfo4;


    @Before
    public void setUp() {
        initMocks(this);

        testJobBasicInfo1 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_ACTIVE, JobBasicInfo.STATUS_OK, "myCronJobEvent-myJobIdKey_CronJob",
                DEFAULT_GROUP, "2002-01-01 00:00:00", "2010-01-01 00:00:00", "-", "Cron", "0 0 10 * * ?", false
        );

        testJobBasicInfo2 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_FINISHED, JobBasicInfo.STATUS_PAUSED, "myDayOfWeekEvent-myJobIdKey_DayOfWeek",
                DEFAULT_GROUP, "2001-01-01 00:00:00", "2010-01-01 00:00:00", "2011-01-01 00:00:00",
                "Cron", "0 10 12 ? * 4,5", false
        );

        testJobBasicInfo3 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_BLOCKED, "myRepeatingJobEvent-myJobIdKey_RepeatingJob-repeat",
                DEFAULT_GROUP, "2003-01-01 00:00:00", "2010-01-01 00:00:00", "2010-01-01 00:00:00",
                JobBasicInfo.JOBTYPE_REPEATING, "2 / 12", false
        );

        testJobBasicInfo4 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_ERROR, "myRunOnceEvent-myJobIdKey_RunOnce-runonce",
                DEFAULT_GROUP, "2004-01-01 00:00:00", "2010-01-01 00:00:00", "-", JobBasicInfo.JOBTYPE_RUNONCE, "-",
                false
        );
    }

    @Test
    public void shouldGetJobsRecords() throws SchedulerException, SQLException {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        JobsSearchSettings jobsSearchSettings = getDefaultGridSettings();
        when(motechSchedulerDatabaseService.getScheduledJobsBasicInfo(jobsSearchSettings)).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(jobsSearchSettings);

        assertEquals(jobBasicInfos, result.getRows());
        verify(motechSchedulerDatabaseService).getScheduledJobsBasicInfo(jobsSearchSettings);
    }

    @Test
    public void shouldGetAllJobsRecordsWhenNoFiltersSet() throws SchedulerException, SQLException {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        JobsSearchSettings jobsSearchSettings = new JobsSearchSettings();
        when(motechSchedulerDatabaseService.getScheduledJobsBasicInfo(jobsSearchSettings)).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(jobsSearchSettings);

        assertEquals(jobBasicInfos, result.getRows());
        verify(motechSchedulerDatabaseService).getScheduledJobsBasicInfo(jobsSearchSettings);
    }

    @Test
    public void shouldGetJobeDetailedInfo() throws SchedulerException, SQLException {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        JobsSearchSettings jobsSearchSettings = getDefaultGridSettings();

        when(motechSchedulerDatabaseService.getScheduledJobsBasicInfo(jobsSearchSettings)).thenReturn(jobBasicInfos);

        jobsController.retrieveJobInfo(jobsSearchSettings);
        jobsController.retrieveJobDetailedInfo(testJobBasicInfo3);

        verify(motechSchedulerDatabaseService).getScheduledJobDetailedInfo(testJobBasicInfo3);
    }

    private JobsSearchSettings getDefaultGridSettings() {
        JobsSearchSettings jobsSearchSettings = new JobsSearchSettings();

        jobsSearchSettings.setActivity(String.format("%s,%s,%s",
                JobBasicInfo.ACTIVITY_ACTIVE,
                JobBasicInfo.ACTIVITY_FINISHED,
                JobBasicInfo.ACTIVITY_NOTSTARTED
        ));
        jobsSearchSettings.setName("");
        jobsSearchSettings.setPage(0);
        jobsSearchSettings.setRows(10);
        jobsSearchSettings.setSortColumn("");
        jobsSearchSettings.setStatus(String.format("%s,%s,%s,%s",
                JobBasicInfo.STATUS_BLOCKED,
                JobBasicInfo.STATUS_ERROR,
                JobBasicInfo.STATUS_OK,
                JobBasicInfo.STATUS_PAUSED
        ));
        jobsSearchSettings.setSortDirection("asc");
        jobsSearchSettings.setTimeFrom("");
        jobsSearchSettings.setTimeTo("");

        return jobsSearchSettings;
    }

    private List<JobBasicInfo> getTestJobBasicInfos() {
        List<JobBasicInfo> jobBasicInfos = new ArrayList<>();

        jobBasicInfos.add(testJobBasicInfo1);
        jobBasicInfos.add(testJobBasicInfo2);
        jobBasicInfos.add(testJobBasicInfo3);
        jobBasicInfos.add(testJobBasicInfo4);

        return jobBasicInfos;
    }
}
