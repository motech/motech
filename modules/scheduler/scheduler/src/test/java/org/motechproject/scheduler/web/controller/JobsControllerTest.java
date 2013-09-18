package org.motechproject.scheduler.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.JobBasicInfo;
import org.motechproject.scheduler.web.domain.JobsRecords;
import org.motechproject.scheduler.web.domain.JobsGridSettings;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class JobsControllerTest {
    @InjectMocks
    JobsController jobsController = new JobsController();

    @Mock
    MotechSchedulerService motechSchedulerService;

    JobBasicInfo testJobBasicInfo1;
    JobBasicInfo testJobBasicInfo2;
    JobBasicInfo testJobBasicInfo3;
    JobBasicInfo testJobBasicInfo4;


    @Before
    public void setUp() {
        initMocks(this);

        testJobBasicInfo1 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_ACTIVE, JobBasicInfo.STATUS_OK, "myCronJobEvent-myJobIdKey_CronJob",
                "2002-01-01 00:00:00", "2010-01-01 00:00:00", "-", "Cron", "0 0 10 * * ?"
        );

        testJobBasicInfo2 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_FINISHED, JobBasicInfo.STATUS_PAUSED, "myDayOfWeekEvent-myJobIdKey_DayOfWeek",
                "2001-01-01 00:00:00", "2010-01-01 00:00:00", "2011-01-01 00:00:00", "Cron", "0 10 12 ? * 4,5"
        );

        testJobBasicInfo3 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_BLOCKED, "myRepeatingJobEvent-myJobIdKey_RepeatingJob-repeat",
                "2003-01-01 00:00:00", "2010-01-01 00:00:00", "2010-01-01 00:00:00", JobBasicInfo.JOBTYPE_REPEATING, "2 / 12"
        );

        testJobBasicInfo4 = new JobBasicInfo(
                JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_ERROR, "myRunOnceEvent-myJobIdKey_RunOnce-runonce",
                "2004-01-01 00:00:00", "2010-01-01 00:00:00", "-", JobBasicInfo.JOBTYPE_RUNONCE, "-"
        );
    }

    @Test
    public void shouldGetJobsRecords() {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();

        when(motechSchedulerService.getScheduledJobsBasicInfo()).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(getDefaultGridSettings());

        assertEquals(jobBasicInfos, result.getRows());
        verify(motechSchedulerService).getScheduledJobsBasicInfo();
    }

    @Test
    public void shouldFilterJobsByStatus() {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        List<JobBasicInfo> jobBasicInfosFiltered = new ArrayList<>();
        JobsGridSettings jobsGridSettings = getDefaultGridSettings();

        for (JobBasicInfo jobBasicInfo : jobBasicInfos) {
            if (!jobBasicInfo.getStatus().equals(JobBasicInfo.STATUS_OK)) {
                jobBasicInfosFiltered.add(jobBasicInfo);
            }
        }

        jobsGridSettings.setStatus(String.format("%s,%s,%s",
                JobBasicInfo.STATUS_BLOCKED,
                JobBasicInfo.STATUS_ERROR,
                JobBasicInfo.STATUS_PAUSED
        ));

        when(motechSchedulerService.getScheduledJobsBasicInfo()).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(jobsGridSettings);

        assertEquals(jobBasicInfosFiltered, result.getRows());
        verify(motechSchedulerService).getScheduledJobsBasicInfo();
    }

    @Test
    public void shouldFilterJobsByActivity() {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        List<JobBasicInfo> jobBasicInfosFiltered = new ArrayList<>();
        JobsGridSettings jobsGridSettings = getDefaultGridSettings();

        for (JobBasicInfo jobBasicInfo : jobBasicInfos) {
            if (!jobBasicInfo.getActivity().equals(JobBasicInfo.ACTIVITY_NOTSTARTED)) {
                jobBasicInfosFiltered.add(jobBasicInfo);
            }
        }

        jobsGridSettings.setActivity(String.format("%s,%s",
                JobBasicInfo.ACTIVITY_ACTIVE,
                JobBasicInfo.ACTIVITY_FINISHED
        ));

        when(motechSchedulerService.getScheduledJobsBasicInfo()).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(jobsGridSettings);

        assertEquals(jobBasicInfosFiltered, result.getRows());
        verify(motechSchedulerService).getScheduledJobsBasicInfo();
    }

    @Test
    public void shouldSortJobsByStartDate() {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        List<JobBasicInfo> jobBasicInfosSorted = getTestJobBasicInfosSortedByStartDate();
        JobsGridSettings jobsGridSettings = getDefaultGridSettings();

        jobsGridSettings.setSortColumn("startDate");

        when(motechSchedulerService.getScheduledJobsBasicInfo()).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(jobsGridSettings);

        assertEquals(jobBasicInfosSorted, result.getRows());
        verify(motechSchedulerService).getScheduledJobsBasicInfo();
    }

    @Test
    public void shouldSortJobsByEndDate() {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        List<JobBasicInfo> jobBasicInfosSorted = getTestJobBasicInfosSortedByEndDate();
        JobsGridSettings jobsGridSettings = getDefaultGridSettings();

        jobsGridSettings.setSortColumn("endDate");

        when(motechSchedulerService.getScheduledJobsBasicInfo()).thenReturn(jobBasicInfos);

        JobsRecords result = jobsController.retrieveJobInfo(jobsGridSettings);

        assertEquals(jobBasicInfosSorted, result.getRows());
        verify(motechSchedulerService).getScheduledJobsBasicInfo();
    }

    @Test
    public void shouldGetJobeDetailedInfo() {
        List<JobBasicInfo> jobBasicInfos = getTestJobBasicInfos();
        JobsGridSettings jobsGridSettings = getDefaultGridSettings();

        jobsGridSettings.setSortColumn("endDate");

        when(motechSchedulerService.getScheduledJobsBasicInfo()).thenReturn(jobBasicInfos);

        jobsController.retrieveJobInfo(jobsGridSettings);
        jobsController.retrieveJobDetailedInfo(1);

        verify(motechSchedulerService).getScheduledJobDetailedInfo(testJobBasicInfo3);
    }

    private JobsGridSettings getDefaultGridSettings() {
        JobsGridSettings jobsGridSettings = new JobsGridSettings();

        jobsGridSettings.setActivity(String.format("%s,%s,%s",
                JobBasicInfo.ACTIVITY_ACTIVE,
                JobBasicInfo.ACTIVITY_FINISHED,
                JobBasicInfo.ACTIVITY_NOTSTARTED
        ));
        jobsGridSettings.setName("");
        jobsGridSettings.setPage(0);
        jobsGridSettings.setRows(10);
        jobsGridSettings.setSortColumn("");
        jobsGridSettings.setStatus(String.format("%s,%s,%s,%s",
                JobBasicInfo.STATUS_BLOCKED,
                JobBasicInfo.STATUS_ERROR,
                JobBasicInfo.STATUS_OK,
                JobBasicInfo.STATUS_PAUSED
        ));
        jobsGridSettings.setSortDirection("asc");
        jobsGridSettings.setTimeFrom("");
        jobsGridSettings.setTimeTo("");

        return jobsGridSettings;
    }

    private List<JobBasicInfo> getTestJobBasicInfos() {
        List<JobBasicInfo> jobBasicInfos = new ArrayList<>();

        jobBasicInfos.add(testJobBasicInfo1);
        jobBasicInfos.add(testJobBasicInfo2);
        jobBasicInfos.add(testJobBasicInfo3);
        jobBasicInfos.add(testJobBasicInfo4);

        return jobBasicInfos;
    }

    private List<JobBasicInfo> getTestJobBasicInfosSortedByStartDate() {
        List<JobBasicInfo> jobBasicInfos = new ArrayList<>();

        jobBasicInfos.add(testJobBasicInfo2);
        jobBasicInfos.add(testJobBasicInfo1);
        jobBasicInfos.add(testJobBasicInfo3);
        jobBasicInfos.add(testJobBasicInfo4);

        return jobBasicInfos;
    }

    private List<JobBasicInfo> getTestJobBasicInfosSortedByEndDate() {
        List<JobBasicInfo> jobBasicInfos = new ArrayList<>();

        jobBasicInfos.add(testJobBasicInfo3);
        jobBasicInfos.add(testJobBasicInfo2);
        jobBasicInfos.add(testJobBasicInfo1);
        jobBasicInfos.add(testJobBasicInfo4);

        return jobBasicInfos;
    }
}
