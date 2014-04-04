package org.motechproject.osgiit.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.INSTALLED;
import static org.osgi.framework.Bundle.RESOLVED;
import static org.osgi.framework.Bundle.UNINSTALLED;

/**
 * Verify that event listeners are registered and cleared for a bundle.
 * 
 * Checks that event listeners are registered and cleared properly as the
 * motech-osgi-integration-tests bundle transitions through its life cycle.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ListenerBundleLifecycleIT extends BasePaxIT {

    private static final String BUNDLE_NAME = "motech-osgi-integration-tests";

    @Inject
    private EventListenerRegistryService registry;
    @Inject
    private BundleContext bundleContext;

    @Test
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

    @Test
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
        assertEquals(1, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_A));
        assertEquals(1, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_B));
        assertEquals(2, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_TWO_LISTENERS));
    }

    private void verifyListenersCleared() {
        assertEquals(0, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_A));
        assertEquals(0, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_ONE_LISTENER_B));
        assertEquals(0, registry.getListenerCount(SampleEventListener.SUBJECT_FOR_TWO_LISTENERS));
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

}
