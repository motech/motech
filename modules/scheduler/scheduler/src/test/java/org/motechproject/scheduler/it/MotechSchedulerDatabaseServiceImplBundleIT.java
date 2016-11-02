package org.motechproject.scheduler.it;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobDetailedInfo;
import org.motechproject.scheduler.contract.JobsSearchSettings;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MotechSchedulerDatabaseServiceImplBundleIT extends BasePaxIT {

    private static final int CURRENT_YEAR = DateTime.now().getYear();
    private static final String DEFAULT_GROUP = "default-group";

    @Inject
    private BundleContext context;

    @Inject
    private MotechSchedulerService schedulerService;

    @Inject
    private MotechSchedulerDatabaseService databaseService;

    MotechSchedulerFactoryBean motechSchedulerFactoryBean;

    Scheduler scheduler;

    @Before
    public void setUp() {
        setUpSecurityContextForDefaultUser("viewSchedulerJobs");
        motechSchedulerFactoryBean = (MotechSchedulerFactoryBean) getBeanFromBundleContext(context,
                "org.motechproject.motech-scheduler", "motechSchedulerFactoryBean");
        scheduler = motechSchedulerFactoryBean.getQuartzScheduler();
    }

    @After
    public void tearDown() throws SchedulerException {
        schedulerService.unscheduleAllJobs("test_event");
    }

    @Test
    public void shouldGetJobTimes() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 6, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");
            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event", params),
                            "0 0 12 * * ?"
                    ));


            List<DateTime> eventTimes = schedulerService.getScheduledJobTimings("test_event", "job_id",
                    newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                    newDateTime(CURRENT_YEAR + 6, 7, 17, 12, 0, 0));
            assertEquals(asList(
                    newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                    newDateTime(CURRENT_YEAR + 6, 7, 16, 12, 0, 0),
                    newDateTime(CURRENT_YEAR + 6, 7, 17, 12, 0, 0)),
                    eventTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldCheckIFJobIsUIDefined() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 6, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");

            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            null,
                            DateTimeConstants.SECONDS_PER_DAY,
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                            newDateTime(CURRENT_YEAR + 6, 7, 18, 12, 0, 0),
                            false,
                            false,
                            true
                    )
            );

            schedulerService.scheduleRepeatingPeriodJob(
                    new RepeatingPeriodSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                            newDateTime(CURRENT_YEAR + 6, 7, 18, 12, 0, 0),
                            new Period(4, 0, 0, 0),
                            false,
                            false,
                            false
                    )
            );

            List<JobBasicInfo> expectedJobBasicInfos = new ArrayList<>();
            expectedJobBasicInfos.add(
                    new JobBasicInfo(
                            JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_OK,
                            "test_event_2-job_id-repeat",
                            DEFAULT_GROUP,
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-18 12:00:00", CURRENT_YEAR +6),
                            JobBasicInfo.JOBTYPE_REPEATING, "", true
                    )
            );
            expectedJobBasicInfos.add(
                    new JobBasicInfo(
                            JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_OK,
                            "test_event_2-job_id-period",
                            DEFAULT_GROUP,
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-18 12:00:00", CURRENT_YEAR +6),
                            JobBasicInfo.JOBTYPE_PERIOD, "", false
                    )
            );

            int testJobsCount = 0;
            for (JobBasicInfo job : expectedJobBasicInfos) {
                for (JobBasicInfo expectedJob : expectedJobBasicInfos) {
                    if(job.getName().equals(expectedJob.getName())) {
                        testJobsCount+=1;
                        assertEquals(expectedJob.isUiDefined(), job.isUiDefined());
                    }
                }
            }
            assertEquals(2, testJobsCount);

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetScheduledJobsBasicInfo() throws SchedulerException, SQLException {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 6, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");

            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            "0 0 12 * * ?"
                    )
            );

            schedulerService.scheduleRunOnceJob(
                    new RunOnceSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0)
                    )
            );

            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            DateTimeConstants.SECONDS_PER_DAY,
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                            newDateTime(CURRENT_YEAR + 6, 7, 18, 12, 0, 0),
                            false
                    )
            );

            schedulerService.scheduleRepeatingPeriodJob(
                    new RepeatingPeriodSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                            newDateTime(CURRENT_YEAR + 6, 7, 18, 12, 0, 0),
                            new Period(4, 0, 0, 0),
                            false
                    )
            );

            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    if (jobKey.getName().equals("test_event_2-job_id")) {
                        scheduler.pauseJob(jobKey);
                    }
                }
            }

            List<JobBasicInfo> expectedJobBasicInfos = new ArrayList<>();
            expectedJobBasicInfos.add(
                    new JobBasicInfo(
                            JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_PAUSED,
                            "test_event_2-job_id",
                            DEFAULT_GROUP,
                            format("%s-07-15 10:00:00", CURRENT_YEAR + 6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR + 6),
                            "-", JobBasicInfo.JOBTYPE_CRON, "", false
                    )
            );
            expectedJobBasicInfos.add(
                    new JobBasicInfo(
                            JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_OK,
                            "test_event_2-job_id-runonce",
                            DEFAULT_GROUP,
                            format("%s-07-15 12:00:00", CURRENT_YEAR + 6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR + 6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR + 6),
                            JobBasicInfo.JOBTYPE_RUNONCE, "", false
                    )
            );
            expectedJobBasicInfos.add(
                    new JobBasicInfo(
                            JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_OK,
                            "test_event_2-job_id-repeat",
                            DEFAULT_GROUP,
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-18 12:00:00", CURRENT_YEAR +6),
                            JobBasicInfo.JOBTYPE_REPEATING, "", false
                    )
            );
            expectedJobBasicInfos.add(
                    new JobBasicInfo(
                            JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_OK,
                            "test_event_2-job_id-period",
                            DEFAULT_GROUP,
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-15 12:00:00", CURRENT_YEAR +6),
                            format("%s-07-18 12:00:00", CURRENT_YEAR +6),
                            JobBasicInfo.JOBTYPE_PERIOD, "", false
                    )
            );

            List<JobBasicInfo> jobBasicInfos;
            JobsSearchSettings jobsSearchSettings = getGridSettings(0, 10, "name", "asc");
            jobBasicInfos = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);


            int testJobsCount = 0;
            for (JobBasicInfo job : jobBasicInfos) {
                for (JobBasicInfo expectedJob : expectedJobBasicInfos) {
                    if(job.getName().equals(expectedJob.getName())) {
                        testJobsCount+=1;

                        assertEquals(expectedJob.getActivity(), job.getActivity());
                        assertEquals(expectedJob.getStatus(), job.getStatus());
                        assertEquals(expectedJob.getStartDate(), job.getStartDate());
                        assertEquals(expectedJob.getNextFireDate(), job.getNextFireDate());
                    }
                }
            }
            assertEquals(4, testJobsCount);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetScheduledJobsBasicInfoWithSortingAndPagination() throws SchedulerException, SQLException {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 6, 7, 15, 10, 0, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");

            schedulerService.scheduleJob(
                    new CronSchedulableJob(
                            new MotechEvent("test_event_2a", params),
                            "0 0 12 * * ?"
                    )
            );

            schedulerService.scheduleRunOnceJob(
                    new RunOnceSchedulableJob(
                            new MotechEvent("test_event_2b", params),
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0)
                    )
            );

            schedulerService.scheduleRepeatingJob(
                    new RepeatingSchedulableJob(
                            new MotechEvent("test_event_2c", params),
                            DateTimeConstants.SECONDS_PER_DAY,
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0),
                            newDateTime(CURRENT_YEAR + 6, 7, 18, 12, 0, 0),
                            false
                    )
            );

            JobBasicInfo expected = new JobBasicInfo(
                    JobBasicInfo.ACTIVITY_NOTSTARTED, JobBasicInfo.STATUS_OK,
                    "test_event_2a-job_id",
                    DEFAULT_GROUP,
                    format("%s-07-15 10:00:00", CURRENT_YEAR + 6),
                    format("%s-07-15 12:00:00", CURRENT_YEAR + 6),
                    "-", JobBasicInfo.JOBTYPE_CRON, "", false
            );

            List<JobBasicInfo> jobBasicInfos;
            JobsSearchSettings jobsSearchSettings = getGridSettings(2, 2, "name", "desc");
            jobBasicInfos = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);


            assertEquals(expected.getActivity(), jobBasicInfos.get(0).getActivity());
            assertEquals(expected.getStatus(), jobBasicInfos.get(0).getStatus());
            assertEquals(expected.getStartDate(), jobBasicInfos.get(0).getStartDate());
            assertEquals(expected.getNextFireDate(), jobBasicInfos.get(0).getNextFireDate());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetScheduledJobDetailedInfo() throws SchedulerException, SQLException {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 6, 7, 15, 10, 0, 0));

            JobDetailedInfo jobDetailedInfo = null;
            Map<String, Object> params = new HashMap<>();
            params.put(MotechSchedulerService.JOB_ID_KEY, "job_id_2");
            params.put("param1","value1");
            params.put("param2","value2");

            schedulerService.scheduleRunOnceJob(
                    new RunOnceSchedulableJob(
                            new MotechEvent("test_event_2", params),
                            newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0)
                    ));

            JobsSearchSettings jobsSearchSettings = getGridSettings(0, 10, "name", "asc");
            for (JobBasicInfo job : databaseService.getScheduledJobsBasicInfo(jobsSearchSettings)) {
                if (job.getName().equals("test_event_2-job_id_2-runonce")) {
                    jobDetailedInfo = databaseService.getScheduledJobDetailedInfo(job);
                }
            }

            assertNotNull(jobDetailedInfo);
            assertEquals("test_event_2", jobDetailedInfo.getEventInfoList().get(0).getSubject());
            assertEquals(4, jobDetailedInfo.getEventInfoList().get(0).getParameters().size());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldFilterJobsByDate() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 1, 7, 13, 5, 0, 0));
            addTestJobs();

            JobsSearchSettings jobsSearchSettings = getGridSettings(null, null, "name", "asc");
            jobsSearchSettings.setTimeTo(format("%s-03-15 9:30:00", CURRENT_YEAR + 7));
            jobsSearchSettings.setTimeFrom(format("%s-03-15 9:30:00", CURRENT_YEAR + 3));

            List<JobBasicInfo> jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(3, jobs.size());
            assertEquals("test_event_3-job_id3", jobs.get(0).getName());
            assertEquals("test_event_5-job_id5-runonce", jobs.get(1).getName());
            assertEquals("test_event_6-job_id6-repeat", jobs.get(2).getName());

            jobsSearchSettings.setTimeTo(format("%s-03-15 9:30:00", CURRENT_YEAR + 5));
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(2, jobs.size());

            jobsSearchSettings.setTimeFrom("");
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(5, jobs.size());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldReturnAllJobsWhenNoFiltersSet() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 1, 7, 13, 5, 0, 0));
            addTestJobs();

            JobsSearchSettings jobsSearchSettings = new JobsSearchSettings();

            List<JobBasicInfo> jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(6, jobs.size());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldFilterJobsByName() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 1, 7, 13, 5, 0, 0));
            addTestJobs();

            JobsSearchSettings jobsSearchSettings = getGridSettings(null, null, "name", "asc");
            jobsSearchSettings.setName("id1");

            List<JobBasicInfo> jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(1, jobs.size());
            assertEquals(jobs.get(0).getName(), "test_event_1-job_id1");
            int rowCount = databaseService.countJobs(jobsSearchSettings);
            assertEquals(1, rowCount);

            jobsSearchSettings.setName("test_ev");
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(6, jobs.size());
            rowCount = databaseService.countJobs(jobsSearchSettings);
            assertEquals(6, rowCount);

            jobsSearchSettings.setName("test_event_2-job_id2");
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(1, jobs.size());
            assertEquals(jobs.get(0).getName(), "test_event_2-job_id2");
            rowCount = databaseService.countJobs(jobsSearchSettings);
            assertEquals(1, rowCount);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldFilterJobsByActivity() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 1, 7, 13, 5, 0, 0));
            addTestJobs();

            JobsSearchSettings jobsSearchSettings = getGridSettings(null, null, "name", "asc");
            jobsSearchSettings.setActivity(JobBasicInfo.ACTIVITY_NOTSTARTED);

            List<JobBasicInfo> jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(5, jobs.size());

            jobsSearchSettings.setActivity(JobBasicInfo.ACTIVITY_ACTIVE);
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertEquals(1, jobs.size());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldFilterJobsWithSortingAndPagination() {
        try {
            fakeNow(newDateTime(CURRENT_YEAR + 1, 7, 13, 10, 0, 0));
            addTestJobs();

            JobsSearchSettings jobsSearchSettings = getGridSettings(1, 5, "name", "asc");
            jobsSearchSettings.setName("event");
            List<JobBasicInfo> jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(5, jobs.size());
            assertEquals(6, databaseService.countJobs(jobsSearchSettings));
            assertEquals(printJobNames(jobs), "test_event_1-job_id1", jobs.get(0).getName());

            jobsSearchSettings.setPage(2);
            jobsSearchSettings.setName("test");
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(1, jobs.size());
            assertEquals(6, databaseService.countJobs(jobsSearchSettings));

            jobsSearchSettings.setPage(3);
            jobsSearchSettings.setRows(2);
            jobsSearchSettings.setSortDirection("desc");
            jobsSearchSettings.setActivity(JobBasicInfo.ACTIVITY_NOTSTARTED);
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertNotNull(jobs);
            assertEquals(1, jobs.size());
            assertEquals(JobBasicInfo.ACTIVITY_NOTSTARTED, jobs.get(0).getActivity());
            assertEquals(5, databaseService.countJobs(jobsSearchSettings));

            jobsSearchSettings = getGridSettings(1, 2, "name", "asc");
            jobsSearchSettings.setTimeTo(format("%s-03-15 9:30:00", CURRENT_YEAR + 7));
            jobsSearchSettings.setTimeFrom(format("%s-03-15 9:30:00", CURRENT_YEAR + 3));
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertEquals(2, jobs.size());
            assertEquals(printJobNames(jobs), "test_event_3-job_id3", jobs.get(0).getName());
            assertEquals(3, databaseService.countJobs(jobsSearchSettings));

            jobsSearchSettings.setSortDirection("desc");
            jobs = databaseService.getScheduledJobsBasicInfo(jobsSearchSettings);
            assertEquals(2, jobs.size());
            assertEquals(printJobNames(jobs), "test_event_6-job_id6-repeat", jobs.get(0).getName());
            assertEquals(3, databaseService.countJobs(jobsSearchSettings));
        } finally {
            stopFakingTime();
        }
    }

    private void addTestJobs() {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id1");
        // this job should be active
        schedulerService.scheduleDayOfWeekJob(
                new DayOfWeekSchedulableJob(
                        new MotechEvent("test_event_1", params),
                        new DateTime(CURRENT_YEAR - 1, 3, 10, 0, 0),
                        new DateTime(CURRENT_YEAR + 2, 3, 22, 0, 0),
                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Thursday),
                        new Time(10, 10),
                        false)
        );
        params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id2");
        schedulerService.scheduleDayOfWeekJob(
                new DayOfWeekSchedulableJob(
                        new MotechEvent("test_event_2", params),
                        new DateTime(CURRENT_YEAR + 1, 7, 10, 0, 0),
                        new DateTime(CURRENT_YEAR + 3, 7, 22, 0, 0),
                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday, DayOfWeek.Thursday, DayOfWeek.Friday),
                        new Time(10, 10),
                        false)
        );
        params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id3");
        schedulerService.scheduleDayOfWeekJob(
                new DayOfWeekSchedulableJob(
                        new MotechEvent("test_event_3", params),
                        new DateTime(CURRENT_YEAR + 4, 7, 10, 0, 0),
                        new DateTime(CURRENT_YEAR + 5, 7, 22, 0, 0),
                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Thursday),
                        new Time(10, 10),
                        false)
        );
        params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id4");
        schedulerService.scheduleJob(
                new CronSchedulableJob(
                        new MotechEvent("test_event_4", params),
                        "0 0 12 * * ?"
                )
        );
        params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id5");
        schedulerService.scheduleRunOnceJob(
                new RunOnceSchedulableJob(
                        new MotechEvent("test_event_5", params),
                        newDateTime(CURRENT_YEAR + 6, 7, 15, 12, 0, 0)
                )
        );
        params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id6");
        schedulerService.scheduleRepeatingJob(
                new RepeatingSchedulableJob(
                        new MotechEvent("test_event_6", params),
                        DateTimeConstants.SECONDS_PER_DAY,
                        newDateTime(CURRENT_YEAR + 4, 7, 15, 12, 0, 0),
                        newDateTime(CURRENT_YEAR + 4, 7, 18, 12, 0, 0),
                        false
                )
        );

    }

    private JobsSearchSettings getGridSettings(Integer page, Integer rows, String sortColumn, String direction) {
        JobsSearchSettings jobsSearchSettings = new JobsSearchSettings();

        jobsSearchSettings.setActivity(format("%s,%s,%s",
                JobBasicInfo.ACTIVITY_ACTIVE,
                JobBasicInfo.ACTIVITY_FINISHED,
                JobBasicInfo.ACTIVITY_NOTSTARTED
        ));
        jobsSearchSettings.setName("");
        jobsSearchSettings.setPage(page);
        jobsSearchSettings.setRows(rows);
        jobsSearchSettings.setSortColumn(sortColumn);
        jobsSearchSettings.setStatus(format("%s,%s,%s,%s",
                JobBasicInfo.STATUS_BLOCKED,
                JobBasicInfo.STATUS_ERROR,
                JobBasicInfo.STATUS_OK,
                JobBasicInfo.STATUS_PAUSED
        ));
        jobsSearchSettings.setSortDirection(direction);
        jobsSearchSettings.setTimeFrom("");
        jobsSearchSettings.setTimeTo("");

        return jobsSearchSettings;
    }

    private String printJobNames(Collection<JobBasicInfo> jobs) {
        return "Job names: " + extract(jobs, on(JobBasicInfo.class).getName());
    }
}
