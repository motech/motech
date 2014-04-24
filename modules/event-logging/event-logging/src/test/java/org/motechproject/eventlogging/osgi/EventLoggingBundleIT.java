package org.motechproject.eventlogging.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.eventlogging.service.EventLoggingServiceManager;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EventLoggingBundleIT extends BasePaxIT {

    public static final String TEST_EVENT_SUBJECT = "EventLoggingBundleIT";

    @Inject
    private EventLoggingServiceManager eventLoggingServiceManager;
    @Inject
    private EventRelay eventRelay;

    @Override
    protected boolean shouldFakeModuleStartupEvent() {
        return true;
    }

    @Test
    public void testEventLoggingServiceManager() throws InterruptedException {
        TestEventLoggingService eventLoggingService = new TestEventLoggingService();
        eventLoggingServiceManager.registerEventLoggingService(eventLoggingService);
        eventRelay.sendEventMessage(new MotechEvent(TEST_EVENT_SUBJECT));

        synchronized (eventLoggingService) {
            eventLoggingService.wait(5000);
        }
        assertTrue("Event not logged.", eventLoggingService.isLogged());
    }
}
