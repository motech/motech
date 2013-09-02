package org.motechproject.osgiit.listener;

import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.osgiit.listener.SampleEventListener;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.RESOLVED;
import static org.osgi.framework.Bundle.INSTALLED;
import static org.osgi.framework.Bundle.UNINSTALLED;

/**
 * Verify that event listeners are registered and cleared for a bundle.
 * 
 * Checks that event listeners are registered and cleared properly as the
 * motech-osgi-integration-tests bundle transitions through its life cycle.
 */
public class ListenerBundleLifecycleIT extends BaseOsgiIT {
    private static final String BUNDLE_NAME = "motech-osgi-integration-tests";

    public void testListenerRegistrationStopStart() throws Exception {
        verifyListenersRegistered();

        Bundle testBundle = getTestingBundle();

        testBundle.stop();
        waitForBundleState(testBundle, RESOLVED);
        verifyListenersCleared();

        testBundle.start();
        waitForBundleState(testBundle, ACTIVE);
        verifyListenersRegistered();
    }

    public void testListenerRegistrationUninstallInstall() throws Exception {
        verifyListenersRegistered();

        Bundle testBundle = getTestingBundle();
        String testingUtilsLocation = testBundle.getLocation();

        testBundle.uninstall();
        waitForBundleState(testBundle, UNINSTALLED);
        verifyListenersCleared();

        testBundle = bundleContext.installBundle(testingUtilsLocation);
        waitForBundleState(testBundle, INSTALLED);
        testBundle.start();
        waitForBundleState(testBundle, ACTIVE);
        verifyListenersRegistered();
    }

    private void verifyListenersRegistered() {
        EventListenerRegistry registry = getEventRegistry();
        assertEquals(1, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_A));
        assertEquals(1, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_B));
        assertEquals(2, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_TWO_LISTENERS));
    }

    private void verifyListenersCleared() {
        EventListenerRegistry registry = getEventRegistry();
        assertEquals(0, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_A));
        assertEquals(0, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_B));
        assertEquals(0, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_TWO_LISTENERS));
    }

    private EventListenerRegistry getEventRegistry() {
        ServiceReference registryReference = bundleContext.getServiceReference(EventListenerRegistryService.class
                .getName());
        assertNotNull(registryReference);
        EventListenerRegistry registry = (EventListenerRegistry) bundleContext.getService(registryReference);
        assertNotNull(registry);
        return registry;
    }

    public Bundle getTestingBundle() {
        Bundle testBundle = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            if (null != bundle.getSymbolicName() && bundle.getSymbolicName().contains(BUNDLE_NAME)
                    && UNINSTALLED != bundle.getState()) {
                testBundle = bundle;
                break;
            }
        }
        assertNotNull(testBundle);
        return testBundle;
    }

    private void waitForBundleState(final Bundle bundle, final int state) throws Exception {
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return state == bundle.getState();
            }
        }, 2000).start();
        assertEquals(state, bundle.getState());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/osgi/listenerBundleLifecycleITContext.xml" };
    }
}
