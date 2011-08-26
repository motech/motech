package org.motechproject.scheduletracking.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.contract.EnrollmentRequest;
import org.motechproject.scheduletracking.api.dao.AllEnrollments;
import org.motechproject.scheduletracking.api.dao.AllTrackedSchedules;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationScheduleTrackingAPI.xml", "classpath*:applicationPlatformScheduler.xml"})
public class ScheduleTrackingServiceTest {
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllEnrollments allEnrollments;
    @Autowired
    private AllTrackedSchedules allTrackedSchedules;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    private EnrollmentRequest enrollmentRequest;
    private Scheduler scheduler;

    @Before
    public void setup() {
        enrollmentRequest = new EnrollmentRequest("job_001", "IPTI Schedule", "sd", 1, new Time(1, 1));
        scheduler = schedulerFactoryBean.getScheduler();
    }

    @Test
    public void autoWiring() {
        assertNotNull(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollSchedule() throws SchedulerException {
        int initialSize = allEnrollments.getAll().size();
        scheduleTrackingService.enroll(enrollmentRequest);
        assertThat(allEnrollments.getAll().size(), is(greaterThan(initialSize)));
        assertThat(scheduler.getJobNames("default").length, is(equalTo(1)));
    }

}
