package org.motechproject.event.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({EventBundleIT.class, EventHandlerAnnotationProcessorBundleIT.class,
        EventRelayClassLoaderBundleIT.class, MotechEventTransformerIT.class, ServerEventRelayIT.class})
public class EventIntegrationTests {
}
