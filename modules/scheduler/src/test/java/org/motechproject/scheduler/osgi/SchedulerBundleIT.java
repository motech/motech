package org.motechproject.scheduler.osgi;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SchedulerBundleIT extends BasePaxIT {

    private String TEST_SUBJECT = "SchedulerBundleIT" + UUID.randomUUID().toString();

    @Inject @org.ops4j.pax.exam.util.Filter(timeout=360000)
    private EventListenerRegistryService eventRegistry;

    @Inject
    private MotechSchedulerService schedulerService;

    @Test
    public void testRunOnceJob() throws InterruptedException {
        final List<String> receivedEvents = new ArrayList<>();

        eventRegistry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                synchronized (receivedEvents) {
                    receivedEvents.add(event.getSubject());
                    receivedEvents.notify();
                }
            }

            @Override
            public String getIdentifier() {
                return TEST_SUBJECT;
            }
        }, TEST_SUBJECT);

        final MotechEvent motechEvent = new MotechEvent(TEST_SUBJECT);
        motechEvent.getParameters().put(MotechSchedulerService.JOB_ID_KEY, "jobId");
        schedulerService.unscheduleAllJobs("SchedulerBundleIT");
        schedulerService.scheduleRunOnceJob(new RunOnceSchedulableJob(motechEvent, DateTime.now().plusSeconds(5).toDate()));
        synchronized (receivedEvents) {
            System.out.print("\nEvent waiting " + new Date() + "\n");
            receivedEvents.wait(15000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(receivedEvents.get(0), TEST_SUBJECT);
    }
}
