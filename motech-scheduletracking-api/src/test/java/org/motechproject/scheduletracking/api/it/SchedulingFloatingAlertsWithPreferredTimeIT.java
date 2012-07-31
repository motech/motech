package org.motechproject.scheduletracking.api.it;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class SchedulingFloatingAlertsWithPreferredTimeIT {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    MotechSchedulerService schedulerService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private AllEnrollments allEnrollments;

    Scheduler scheduler;

    @Before
    public void setup() {
        scheduler = schedulerFactoryBean.getScheduler();
    }

    @After
    public void teardown() {
        schedulerService.unscheduleAllJobs("org.motechproject.scheduletracking");
        allEnrollments.removeAll();
    }

    @Test
    public void shouldScheduleFloatingAlertsAtPreferredAlertTime() throws SchedulerException, URISyntaxException, IOException {

        String enrollmentId = scheduleTrackingService.enroll(new EnrollmentRequest(
                "abcde",
                "schedule_with_floating_alerts",
                new Time(8, 20),
                newDate(2050, 5, 10), new Time(9, 0),
                newDate(2050, 5, 10), new Time(9, 0),
                "milestone1",
                null));

        List<DateTime> fireTimes = getFireTimes(format("org.motechproject.scheduletracking.api.milestone.alert-%s.0-repeat", enrollmentId)) ;
        assertEquals(asList(
                newDateTime(2050, 5, 17, 8, 20, 0),
                newDateTime(2050, 5, 18, 8, 20, 0),
                newDateTime(2050, 5, 19, 8, 20, 0),
                newDateTime(2050, 5, 20, 8, 20, 0)),
                fireTimes);
    }

    @Test
    public void shouldFloatTheAlertsForDelayedEnrollment() throws SchedulerException, URISyntaxException, IOException {
        try {
            fakeNow(newDateTime(2050, 5, 10, 10, 0, 0));
            String enrollmentId = scheduleTrackingService.enroll(new EnrollmentRequest(
                    "abcde",
                    "schedule_with_floating_alerts",
                    new Time(9, 0),
                    newDate(2050, 5, 2), new Time(11, 0),
                    newDate(2050, 5, 2), new Time(11, 0),
                    "milestone1",
                    null));

            List<DateTime> fireTimes = getFireTimes(format("org.motechproject.scheduletracking.api.milestone.alert-%s.0-repeat", enrollmentId)) ;
            assertEquals(asList(
                    newDateTime(2050, 5, 11, 9, 0, 0),
                    newDateTime(2050, 5, 12, 9, 0, 0),
                    newDateTime(2050, 5, 13, 9, 0, 0),
                    newDateTime(2050, 5, 14, 9, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldFloatTheAlertsForDelayedEnrollmentInTheGivenSpaceLeft() throws SchedulerException, URISyntaxException, IOException {

        DateTime now = now();
        Time twoHoursBack = new Time(now.minusHours(2).getHourOfDay(), 0);
        Time twoHoursLater = new Time(now.plusHours(2).getHourOfDay(), 0);
        String enrollmentId = scheduleTrackingService.enroll(new EnrollmentRequest(
                "abcde",
                "schedule_with_floating_alerts",
                twoHoursBack,
                now.toLocalDate().minusDays(11), twoHoursLater,
                now.toLocalDate().minusDays(11), twoHoursLater,
                "milestone1",
                null));

        List<DateTime> fireTimes = getFireTimes(format("org.motechproject.scheduletracking.api.milestone.alert-%s.0-repeat", enrollmentId)) ;
        assertEquals(asList(
                newDateTime(now.toLocalDate().plusDays(1), twoHoursBack),
                newDateTime(now.toLocalDate().plusDays(2), twoHoursBack),
                newDateTime(now.toLocalDate().plusDays(3), twoHoursBack)),
                fireTimes);
    }

    @Test
    public void shouldScheduleSecondMilestoneAlertsForToday() throws IOException, URISyntaxException, SchedulerException {

        DateTime now = now();
        try {
            fakeNow(newDateTime(2050, 5, 20, 10, 0, 0));
            String enrollmentId = scheduleTrackingService.enroll(new EnrollmentRequest(
                    "abcde",
                    "schedule_with_floating_alerts",
                    new Time(9, 0),
                    newDate(2050, 5, 9), new Time(11, 0),
                    newDate(2050, 5, 9), new Time(11, 0),
                    "milestone1",
                    null));
            scheduleTrackingService.fulfillCurrentMilestone("abcde", "schedule_with_floating_alerts", newDate(2050, 5, 18), new Time(11, 0));

            List<DateTime> fireTimes = getFireTimes(format("org.motechproject.scheduletracking.api.milestone.alert-%s.1-repeat", enrollmentId)) ;
            assertEquals(asList(
                    newDateTime(2050, 5, 21, 9, 0, 0),
                    newDateTime(2050, 5, 22, 9, 0, 0),
                    newDateTime(2050, 5, 23, 9, 0, 0)),
                    fireTimes);
        } finally {
            stopFakingTime();
        }
    }


    private List<DateTime> getFireTimes(String key) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(key, "default");
        List<DateTime> fireTimes = new ArrayList<DateTime>();
        Date nextFireTime = trigger.getNextFireTime();
        while (nextFireTime != null) {
            fireTimes.add(newDateTime(nextFireTime));
            nextFireTime = trigger.getFireTimeAfter(nextFireTime);
        }
        return fireTimes;
    }
}
