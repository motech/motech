package org.motechproject.event.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.osgi.TestHandler;
import org.motechproject.event.osgi.TestHandlerProxied;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EventHandlerAnnotationProcessorBundleIT extends BasePaxIT {

    @Inject
    private EventListenerRegistryService eventListenerRegistry;
    @Inject
    private EventRelay eventRelay;

    @Test
    public void testThatBeansWithMotechListenerAnnotationsAreBeingRegistered() {
        assertTrue(eventListenerRegistry.hasListener(TestHandler.TEST_SUBJECT));
    }

    @Test
    public void shouldRegisterOnlyOneListenerForProxiedBean() {
        assertEquals(1, eventListenerRegistry.getListenerCount(TestHandlerProxied.TEST_HANDLER_PROXIED_SUBJECT));
    }
}
