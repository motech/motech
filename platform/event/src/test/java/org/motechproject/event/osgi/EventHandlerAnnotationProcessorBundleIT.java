package org.motechproject.event.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EventHandlerAnnotationProcessorBundleIT extends BasePaxIT {

    @Inject
    private EventListenerRegistryService eventListenerRegistry;
    @Inject
    private EventRelay eventRelay;

    @Test
    public void testThatBeansWithMotechListenerAnnotationsAreBeingRegistered() throws InterruptedException {
        eventRelay.sendEventMessage(new MotechEvent(TestHandler.TEST_SUBJECT));

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !eventListenerRegistry.hasListener(TestHandler.TEST_SUBJECT);
            }
        }, 2000).start();

        assertTrue(eventListenerRegistry.hasListener(TestHandler.TEST_SUBJECT));
    }
}
