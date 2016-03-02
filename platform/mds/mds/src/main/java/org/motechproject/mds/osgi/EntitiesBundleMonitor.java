package org.motechproject.mds.osgi;

import org.motechproject.mds.exception.MdsEntityWireException;
import org.motechproject.mds.exception.MdsException;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeToString;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

/**
 * The <code>EntitiesBundleMonitor</code> is used to monitor state of the entities bundle and its
 * context. It is also used to install or update the entities bundle by
 * {@link org.motechproject.mds.service.JarGeneratorService}.
 * <p/>
 * The important thing is that the class waits until the given status of the entities bundle is reached.
 */
@Component
public class EntitiesBundleMonitor implements BundleListener, ServiceListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitiesBundleMonitor.class);
    private static final Integer MAX_WAIT_COUNT = 2500;
    private static final Long WAIT_TIME = 200L;

    private final Object lock = new Object();

    private BundleContext bundleContext;

    private boolean bundleStarted;
    private boolean bundleInstalled;
    private boolean bundleUninstalled;
    private boolean contextInitialized;

    /**
     * Initialises the monitor.
     * <p/>
     * This methods adds the entities bundle monitor to the bundle context as bundle and service
     * listener. Thanks to that, the class is able to monitor state of the entities bundle and its
     * context.
     * <p/>
     * Another thing is to check if the entities bundle already exists in the bundle context. If yes
     * then it has to be stopped and uninstalled and its jar file should be removed to avoid problem
     * with first generating the bundle.
     *
     * @throws IOException if an I/O error occurs
     * @see org.motechproject.mds.service.JarGeneratorService
     */
    public void init() throws IOException {
        LOGGER.debug("Adding entities bundle monitor as bundle/service listener to bundle context");

        bundleContext.addBundleListener(this);
        bundleContext.addServiceListener(this);

        LOGGER.debug("Added entities bundle monitor as bundle/service listener to bundle context");

        Bundle entitiesBundle = getEntitiesBundle();

        if (null != entitiesBundle) {
            stopEntitiesBundle();
            uninstall();
        }

        String location = bundleLocation();
        Path path = Paths.get(location);

        if (Files.exists(path)) {
            LOGGER.info("Removing the entities bundle jar");
            Files.deleteIfExists(path);
            LOGGER.info("Removed the entities bundle jar");
        }
    }

    /**
     * Receives notification that the entities bundle has had a lifecycle change and checks it
     * current status.
     *
     * @param event The {@code BundleEvent}.
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        if (isEntities(event.getBundle())) {
            LOGGER.info("Entities Bundle Status: {}", nullSafeToString(event));

            synchronized (lock) {
                int type = event.getType();

                bundleStarted = type == BundleEvent.STARTED;
                bundleInstalled = type == BundleEvent.INSTALLED;
                bundleUninstalled = type == BundleEvent.UNINSTALLED;
            }
        }
    }

    /**
     * Receives notification that the context of the entities bundle has had a lifecycle change and
     * checks it current status.
     *
     * @param event The {@code ServiceEvent} object.
     */
    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference<?> reference = event.getServiceReference();

        if (isEntities(reference.getBundle())) {
            Object service = bundleContext.getService(reference);

            if (service instanceof ApplicationContext) {
                LOGGER.trace(
                        "The Entities Bundle Application Context Status: {}",
                        nullSafeToString(event)
                );

                synchronized (lock) {
                    contextInitialized = event.getType() != ServiceEvent.UNREGISTERING;

                    if (contextInitialized) {
                        LOGGER.info("The entities bundle context has been initialized");
                    } else {
                        LOGGER.info("The entities bundle context has been unregistered");
                    }
                }
            }
        }
    }

    /**
     * Waits until the entities bundle will be started and its context will be initialized.
     */
    public void waitForEntitiesContext() {
        LOGGER.info("Waiting for entities context");

        waitUntil(new Condition() {
            @Override
            public boolean await() {
                return !bundleStarted || !contextInitialized;
            }
        }, "started and its context will be initialized");

        LOGGER.info("Done waiting for entities context");
    }

    /**
     * Starts or updates the entities bundle from the given {@code File}.
     *
     * @param src         file that points to an entities bundle jar
     * @param startBundle {@code true} if the generated bundle should start;
     *                    otherwise {@code false}.
     * @see org.motechproject.mds.service.JarGeneratorService
     */
    public void start(File src, boolean startBundle) {
        LOGGER.debug("Starting bundle from: {}", src);

        try (InputStream stream = new FileInputStream(src)) {
            Bundle entitiesBundle = getEntitiesBundle();

            if (entitiesBundle == null) {
                LOGGER.info("Entities bundle does not exist");
                install(stream);
            } else if (Bundle.INSTALLED == entitiesBundle.getState()) {
                LOGGER.info("Entities bundle exists and it is installed");
                update(stream);
            } else {
                LOGGER.info("Entities bundle exists and it is resolved");
                stopEntitiesBundle();
                update(stream);
            }

            if (startBundle) {
                start();
            }
        } catch (IOException e) {
            throw new MdsException("Unable to read temporary entities bundle", e);
        }
    }

    /**
     * Gets the default bundle location.
     *
     * @return string that represent path to the bundle jar file.
     */
    public String bundleLocation() {
        String userHome = System.getProperty("user.home");
        FileSystem fileSystem = FileSystems.getDefault();

        Path path = fileSystem.getPath(userHome, ".motech/bundles", "mds-entities.jar");
        Path absolutePath = path.toAbsolutePath();

        return absolutePath.toString();
    }

    /**
     * Starts the entities bundle and waits until it will be started and its context will be
     * initialized.
     */
    public void start() {
        LOGGER.info("Starting the entities bundle");

        try {
            Bundle entitiesBundle = getEntitiesBundle();
            if (entitiesBundle != null && entitiesBundle.getState() != Bundle.STARTING
                    && entitiesBundle.getState() != Bundle.ACTIVE) {
                entitiesBundle.start();
            } else {
                LOGGER.warn("No entities bundle to start");
                return;
            }
        } catch (BundleException e) {
            if (e.getType() == BundleException.RESOLVE_ERROR) {
                throw new MdsEntityWireException(e);
            } else {
                throw new MdsException("Unable to start the entities bundle", e);
            }
        }

        waitUntil(new Condition() {
            @Override
            public boolean await() {
                return !bundleStarted;
            }
        }, "started");

        LOGGER.info("Started the entities bundle");
        waitForEntitiesContext();
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private boolean isEntities(Bundle bundle) {
        return MDS_ENTITIES_SYMBOLIC_NAME.equalsIgnoreCase(bundle.getSymbolicName());
    }

    private void install(InputStream stream) {
        LOGGER.info("Installing the entities bundle");

        try {
            bundleContext.installBundle(bundleLocation(), stream);
        } catch (BundleException e) {
            throw new MdsException("Unable to install the entities bundle", e);
        }

        waitUntil(new Condition() {
            @Override
            public boolean await() {
                return !bundleInstalled;
            }
        }, "installed");

        LOGGER.info("Installed the entities bundle");
    }

    /**
     * Stops the entities bundle and waits until it will be resolved.
     */
    public void stopEntitiesBundle() {
        LOGGER.info("Stopping the entities bundle");

        try {
            Bundle entitiesBundle = getEntitiesBundle();
            if (entitiesBundle != null) {
                entitiesBundle.stop();
            } else {
                LOGGER.warn("No entities bundle to stop");
                return;
            }
        } catch (BundleException e) {
            throw new MdsException("Unable to stop the entities bundle", e);
        }

        waitUntil(new Condition() {
            @Override
            public boolean await() {
                return !isEntitiesBundleState(Bundle.RESOLVED);
            }
        }, "stopped");

        LOGGER.info("Stopped the entities bundle");
    }

    private void uninstall() {
        LOGGER.info("Uninstalling the entities bundle");

        try {
            Bundle entitiesBundle = getEntitiesBundle();
            if (entitiesBundle != null) {
                entitiesBundle.uninstall();
            } else {
                LOGGER.warn("No entities bundle to uninstall");
            }
        } catch (BundleException e) {
            throw new MdsException("Unable to uninstall the entities bundle", e);
        }

        waitUntil(new Condition() {
            @Override
            public boolean await() {
                return !bundleUninstalled;
            }
        }, "uninstalled");

        LOGGER.info("Uninstalled the entities bundle");
    }

    private void update(InputStream stream) {
        LOGGER.info("Updating the entities bundle");

        try {
            Bundle entitiesBundle = getEntitiesBundle();
            if (entitiesBundle != null) {
                entitiesBundle.update(stream);
            } else {
                throw new MdsException("No entities bundle to update, unable to update entities");
            }
        } catch (BundleException e) {
            throw new MdsException("Unable to update the entities bundle", e);
        }

        waitUntil(new Condition() {
            @Override
            public boolean await() {
                return !isEntitiesBundleState(Bundle.INSTALLED);
            }
        }, "updated");

        LOGGER.info("Updated the entities bundle");
    }

    private Bundle getEntitiesBundle() {
        return MdsBundleHelper.findMdsEntitiesBundle(bundleContext);
    }

    private void waitUntil(Condition condition, String status) {
        int count = 0;

        synchronized (lock) {
            while (condition.await() && count < MAX_WAIT_COUNT) {
                LOGGER.trace(String.format("We are waiting for bundle status, condition.await is %b, count is %d and MAX_WAIT_COUNT is %d",
                        condition.await(), count, MAX_WAIT_COUNT));
                LOGGER.trace("[{}/{}] Wait {} milliseconds until the entities bundle will be {}",
                        count + 1, MAX_WAIT_COUNT, WAIT_TIME, status);

                try {
                    lock.wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while waiting", e);
                }

                ++count;
            }

            LOGGER.trace(String.format("We finished waiting for bundle status, condition.await is %b, count is %d and MAX_WAIT_COUNT is %d",
                    condition.await(), count, MAX_WAIT_COUNT));

            if (condition.await()) {
                throw new IllegalStateException("timeout");
            }
        }
    }

    private boolean isEntitiesBundleState(int bundleState) {
        Bundle entitiesBundle = getEntitiesBundle();
        return entitiesBundle != null && entitiesBundle.getState() == bundleState;
    }

    private interface Condition {
        boolean await();
    }
}
