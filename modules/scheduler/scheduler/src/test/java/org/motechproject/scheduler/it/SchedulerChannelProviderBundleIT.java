package org.motechproject.scheduler.it;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.scheduler.tasks.SchedulerChannelProvider;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.SchedulerException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SchedulerChannelProviderBundleIT extends BasePaxIT {

    private static final String TEST_EVENT = "test-event";

    @Inject
    private BundleContext bundleContext;
    
    @Inject
    private MotechSchedulerService schedulerService;

    private SchedulerChannelProvider channelProvider;

    @Before
    public void setUp() {
        if (channelProvider == null) {
            channelProvider = (SchedulerChannelProvider) bundleContext.getService(
                    bundleContext.getServiceReference("org.motechproject.tasks.service.DynamicChannelProvider")
            );
        }

        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, "job_id");

        schedulerService.scheduleJob(
                new CronSchedulableJob(
                        new MotechEvent(TEST_EVENT, params),
                        "0 0 12 * * ?"
                )
        );

        schedulerService.scheduleRunOnceJob(
                new RunOnceSchedulableJob(
                        new MotechEvent(TEST_EVENT, params),
                        DateTime.now().plusDays(1)
                )
        );

        schedulerService.scheduleRepeatingJob(
                new RepeatingSchedulableJob(
                        new MotechEvent(TEST_EVENT, params),
                        DateTimeConstants.SECONDS_PER_DAY,
                        DateTime.now().plusHours(1),
                        DateTime.now().plusHours(3),
                        false
                )
        );
    }

    @Test
    public void shouldGetTriggers() throws SchedulerException {
        List<TriggerEvent> triggers = channelProvider.getTriggers(1, 20);

        assertTrue(triggers.containsAll(getExpectedTriggers()));
    }

    @Test
    public void shouldGetTrigger() {
        TaskTriggerInformation information = new TaskTriggerInformation(
                "Job: test-event-job_id",
                "Channel name",
                "Module name",
                "Module version",
                "test-event-job_id",
                TEST_EVENT
        );

        TriggerEvent trigger = channelProvider.getTrigger(information);

        assertEquals(getExpectedTrigger(), trigger);
    }

    @Test
    public void shouldCountTrigger() {
        assertTrue(channelProvider.countTriggers() >= 3);
    }

    private TriggerEvent getExpectedTrigger() {

        List<EventParameter> parameters = new ArrayList<>();
        parameters.add(new EventParameter("scheduler.jobId", "JobID"));

        return new TriggerEvent(
                "Job: test-event-job_id",
                "test-event-job_id",
                null,
                parameters,
                TEST_EVENT
        );
    }

    private List<TriggerEvent> getExpectedTriggers() {
        List<TriggerEvent> triggers = new ArrayList<>();

        List<EventParameter> parameters = new ArrayList<>();
        parameters.add(new EventParameter("scheduler.jobId", "JobID"));

        triggers.add(new TriggerEvent(
                "Job: test-event-job_id",
                "test-event-job_id",
                null,
                parameters,
                TEST_EVENT
        ));

        triggers.add(new TriggerEvent(
                "Job: test-event-job_id-repeat",
                "test-event-job_id-repeat",
                null,
                parameters,
                TEST_EVENT
        ));

        triggers.add(new TriggerEvent(
                "Job: test-event-job_id-runonce",
                "test-event-job_id-runonce",
                null,
                parameters,
                TEST_EVENT
        ));

        return triggers;
    }

    @After
    public void tearDown() throws SchedulerException {
        schedulerService.unscheduleAllJobs(TEST_EVENT);
    }
}
