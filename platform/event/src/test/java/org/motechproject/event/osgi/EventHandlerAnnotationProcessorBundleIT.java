package org.motechproject.event.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class EventHandlerAnnotationProcessorBundleIT extends BaseOsgiIT {


    public void testThatBeansWithMotechListenerAnnotationsAreBeingRegistered() throws InterruptedException {

        ServiceReference listenerServiceRef = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
        assertNotNull(listenerServiceRef);
        final EventListenerRegistryService eventListenerRegistry = (EventListenerRegistryService) bundleContext.getService(listenerServiceRef);


        ServiceReference eventRelayServiceRef = bundleContext.getServiceReference(EventRelay.class.getName());
        assertNotNull(eventRelayServiceRef);

        EventRelay eventRelay = (EventRelay) bundleContext.getService(eventRelayServiceRef);
        eventRelay.sendEventMessage(new MotechEvent(TestHandler.TEST_SUBJECT));


        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !eventListenerRegistry.hasListener(TestHandler.TEST_SUBJECT);
            }
        }, 2000).start();

        assertTrue(eventListenerRegistry.hasListener(TestHandler.TEST_SUBJECT));

    }


    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.event",
                "org.motechproject.event.listener",
                "org.motechproject.event.listener.annotations");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testEventBundleContext.xml"};
    }
}
