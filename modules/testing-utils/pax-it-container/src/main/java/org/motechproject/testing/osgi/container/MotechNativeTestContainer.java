package org.motechproject.testing.osgi.container;

import org.apache.commons.lang.reflect.MethodUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.server.osgi.PlatformConstants;
import org.motechproject.server.osgi.status.PlatformStatusManager;
import org.ops4j.pax.exam.Constants;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.ops4j.pax.swissbox.tracker.ServiceLookup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * This is the implementation of {@link org.ops4j.pax.exam.TestContainer} used in our OSGi tests. We extend the
 * {@link org.ops4j.pax.exam.nat.internal.NativeTestContainer}. The reason for providing our own container is that
 * the test probe would start up too early, namely before MDS was done processing the tested bundle. The refresh coming
 * from MDS would then not work well with JUnit testing. The purpose of this extension is to avoid this. In order to
 * achieve the desired result, we simply start the probe only after the tested bundle started(or after a timeout).
 * The container can also fake the module startup event, so that the server-bundle is not required to start module startup.
 * The information required for using the extended features of this container must be passed as options from the configuration
 * method of the test(The BasePaxIT class handles this).
 */
public class MotechNativeTestContainer
        extends org.ops4j.pax.exam.nat.internal.NativeTestContainer { // NOPMD - we have to extend this internal class

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechNativeTestContainer.class);

    private static final int WAIT_PERIOD = 1500;
    private static final int MAX_WAIT_RETRIES = 150;

    private static final String TESTED_SYMBOLIC_NAME = "org.motechproject.testing.osgi.TestedSymbolicName";
    private static final String FAKE_MODULE_STARTUP_EVENT = "org.motechproject.testing.osgi.FakeStartupModulesEvent";
    private static final String CONTEXT_SERVICE_NAME = "org.springframework.context.service.name";

    private long probeId;
    private ExamSystem examSystem;
    private boolean startupEventSent;

    public MotechNativeTestContainer(ExamSystem system, FrameworkFactory frameworkFactory) throws IOException {
        super(system, frameworkFactory);
        examSystem = system;
    }

    @Override
    public synchronized long installProbe(InputStream stream) {
        try {
            Bundle bundle = getFramework().getBundleContext().installBundle("local", stream);

            LOGGER.debug("Installed bundle " + bundle.getSymbolicName() + " as Bundle ID "
                    + bundle.getBundleId());

            setBundleStartLevel(bundle.getBundleId(), Constants.START_LEVEL_TEST_BUNDLE);

            // note that we do not start the probe

            probeId = bundle.getBundleId();

            return probeId;
        } catch (BundleException e) {
            LOGGER.error("Unable to install Test Probe", e);
            return -1;
        }
    }

    @Override
    public synchronized void call(TestAddress address) {
        // fake the startup event if necessary, since the server bundle is not always present
        if (shouldFakeModuleStartupEvent()) {
            fakeModuleStartupEvent();
        }
        // we must wait until the tested bundle is properly started, only after that we start the probe
        waitForTestedBundle();
        startProbe();
        // commence the test
        super.call(address);
    }

    protected void waitForTestedBundle() {
        // the symbolic name of the bundle we must wait for should be registered as a system property
        String symbolicName = getSystemProperty(TESTED_SYMBOLIC_NAME);

        if (symbolicName != null) {
            LOGGER.info("Waiting for {}", symbolicName);

            // first get the tested bundle
            BundleContext bundleContext = getFramework().getBundleContext();
            Bundle bundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, symbolicName);

            // then wait

            if (bundle == null) {
                LOGGER.error("Expected tested bundle {} is not installed", symbolicName);
            } else {
                int retries = 0;
                try {
                    while (!isReady(bundle)) {
                        LOGGER.debug("Waiting for tested bundle {}, {}/{}", bundle, retries, MAX_WAIT_RETRIES);
                        if (startupErrorsPresent(bundleContext)) {
                            throw new TestContainerException("Framework startup error occurred");
                        }
                        if (retries++ >= MAX_WAIT_RETRIES) {
                            throw new TestContainerException("Framework not ready after " + MAX_WAIT_RETRIES * WAIT_PERIOD + " ms");
                        }
                        Thread.sleep(WAIT_PERIOD);
                    }
                    LOGGER.debug("Tested bundle {} is ready", bundle);
                } catch (InterruptedException e) {
                    LOGGER.warn("Thread interrupted while waiting for bundle " + symbolicName, e);
                }
            }
        }
    }

    private boolean isReady(Bundle bundle) {
        if (bundle.getState() == Bundle.ACTIVE) {
            BundleContext bundleContext = getFramework().getBundleContext();
            String filter = String.format("(%s=%s)", CONTEXT_SERVICE_NAME, bundle.getSymbolicName());

            ServiceReference[] refs;
            try {
                refs = bundleContext.getAllServiceReferences(ApplicationContext.class.getName(), filter);
            } catch (InvalidSyntaxException e) {
                throw new TestContainerException("Error during retrieving service references", e);
            }

            return refs != null && refs.length > 0;
        }
        return false;
    }

    private boolean startupErrorsPresent(BundleContext bundleContext) {
        // different classloaders, operating on objects required
        try {
            Object statusManager = ServiceLookup.getService(bundleContext, PlatformStatusManager.class);
            Object currentStatus = MethodUtils.invokeExactMethod(statusManager, "getCurrentStatus", new Object[0]);
            return (boolean) MethodUtils.invokeExactMethod(currentStatus, "errorsOccurred", new Object[0]);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new StatusRetrievalException("Unable to retrieve status from manager", e);
        }
    }

    protected boolean shouldFakeModuleStartupEvent() {
        String fakeEventProperty = getSystemProperty(FAKE_MODULE_STARTUP_EVENT);
        return !startupEventSent && "true".equalsIgnoreCase(fakeEventProperty);
    }

    protected void fakeModuleStartupEvent() {
        // We send the startup event, unfortunately we have to use the OSGi ClassLoaders from non-OSGi context,
        // so there is a bit of magic with ClassLoaders here.

        // First get the service
        Object eventAdmin = ServiceLookup.getService(getFramework().getBundleContext(), EventAdmin.class);
        // Then obtain the right OSGi ClassLoader we will ue
        ClassLoader bundleCl = eventAdmin.getClass().getClassLoader();
        try {
            // This the event class we will use for constructing the event
            Class<?> eventClass = bundleCl.loadClass(Event.class.getName());

            // We have to use the constructor via reflection
            Constructor constructor = eventClass.getConstructor(String.class, Map.class);
            Object event = constructor.newInstance(PlatformConstants.STARTUP_TOPIC, new HashMap<String, Object>());

            // Finally we invoke the send method.
            Method sendEventMethod = eventAdmin.getClass().getMethod("sendEvent", eventClass);
            sendEventMethod.invoke(eventAdmin, event);

            startupEventSent = true;
        } catch (Exception e) {
            LOGGER.error("Error while sending the startup event", e);
        }
    }

    protected void startProbe() {
        Bundle probe = getFramework().getBundleContext().getBundle(probeId);

        if (probe == null) {
            throw new IllegalStateException("Cannot start test. Probe not installed.");
        }

        try {
            probe.start();
        } catch (BundleException e) {
            throw new IllegalStateException("Error while starting test probe", e);
        }
    }

    protected Framework getFramework() {
        // We need the framework field declared in our superclass, but the exam creators did not think about us
        // and made it a private field. No matter, we get it through reflection.
        try {
            Field frameworkField = getClass().getSuperclass().getDeclaredField("framework");
            frameworkField.setAccessible(true);
            return (Framework) ReflectionUtils.getField(frameworkField, this);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to access the framework field", e);
        }
    }

    protected String getSystemProperty(String key) {
        SystemPropertyOption[] systemProperties = examSystem.getOptions(SystemPropertyOption.class);

        if (systemProperties != null) {
            for (SystemPropertyOption systemPropertyOption : systemProperties) {
                if (key.equals(systemPropertyOption.getKey())) {
                    return systemPropertyOption.getValue();
                }
            }
        }

        return null;
    }
}
