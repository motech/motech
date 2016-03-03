package org.motechproject.event.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.osgi.TestHandler;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.wait.ContextPublishedWaitCondition;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EventRelayClassLoaderBundleIT extends BasePaxIT {

    @Inject
    private EventListenerRegistryService eventListenerRegistry;
    @Inject
    private EventRelay eventRelay;
    @Inject
    private BundleContext bundleContext;

    @Test
    public void testThatEventHandlerClassLoaderIsInvokedWithCurrentClassLoaderSetAsEventRelaysClassLoader() throws InterruptedException {
        assertNotNull(eventListenerRegistry);
        assertNotNull(eventRelay);

        new Wait(new ContextPublishedWaitCondition(bundleContext, "org.motechproject.motech-platform-event"), 5000).start();

        eventRelay.sendEventMessage(new MotechEvent(TestHandler.SUBJECT_READ));

        final Properties properties = TestHandler.PROPERTIES;

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return properties.isEmpty();
            }
        }, 2000).start();

        assertTrue(properties.containsKey("message"));
        assertEquals("hello world", properties.get("message"));
    }
}
