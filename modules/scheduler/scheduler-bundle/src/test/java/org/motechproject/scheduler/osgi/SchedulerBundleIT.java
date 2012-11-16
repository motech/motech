package org.motechproject.scheduler.osgi;

import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SchedulerBundleIT extends BaseOsgiIT {

    private static final String TEST_SUBJECT = "Scheduler Bundle IT - 001";

    public void testRunOnceJob() throws InterruptedException {
        final Object waitLock = new Object();
        final List<String> receivedEvents = new ArrayList<>();
        EventListenerRegistryService eventRegistry = (EventListenerRegistryService) getApplicationContext().getBean("eventListenerRegistry");
        eventRegistry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                synchronized (waitLock) {
                    waitLock.notify();
                    receivedEvents.add(event.getSubject());
                }
            }

            @Override
            public String getIdentifier() {
                return TEST_SUBJECT;
            }
        }, TEST_SUBJECT);

        ServiceReference schedulerServiceReference = bundleContext.getServiceReference(MotechSchedulerService.class.getName());
        assertNotNull(schedulerServiceReference);
        MotechSchedulerService schedulerService = (MotechSchedulerService) bundleContext.getService(schedulerServiceReference);
        assertNotNull(schedulerService);
        schedulerService.scheduleRunOnceJob(new RunOnceSchedulableJob(new MotechEvent(TEST_SUBJECT), new Date()));
        synchronized (waitLock) {
            waitLock.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(receivedEvents.get(0), TEST_SUBJECT);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testSchedulerBundleContext.xml"};
    }

    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;
    }
}
