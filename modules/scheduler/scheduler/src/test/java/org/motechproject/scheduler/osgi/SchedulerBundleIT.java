package org.motechproject.scheduler.osgi;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.IdGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class SchedulerBundleIT extends BaseOsgiIT {

    private String TEST_SUBJECT = IdGenerator.id("SchedulerBundleIT");

    public void testRunOnceJob() throws InterruptedException {
        final List<String> receivedEvents = new ArrayList<>();
        EventListenerRegistryService eventRegistry = (EventListenerRegistryService) getApplicationContext().getBean("eventListenerRegistry");
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

        MotechSchedulerService schedulerService = getService(MotechSchedulerService.class);

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

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testSchedulerBundleContext.xml"};
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.scheduler",
                "org.motechproject.scheduler.domain"
        );
    }
}
