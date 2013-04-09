package org.motechproject.event.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class EventRelayClassLoaderBundleIT extends BaseOsgiIT {

    public void testThatEventHandlerClassLoaderIsInvokedWithCurrentClassLoaderSetAsEventRelaysClassLoader() throws InterruptedException {

        EventListenerRegistryService eventListenerRegistry = (EventListenerRegistryService) getApplicationContext().getBean("eventListenerRegistry");
        assertNotNull(eventListenerRegistry);


        EventRelay eventRelay = (EventRelay) getApplicationContext().getBean("eventRelay");
        assertNotNull(eventRelay);

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
