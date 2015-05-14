package org.motechproject.server.osgi.status;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PlatformStatusManager implementation. Acts as an listener for Blueprint events to get notified about
 * modules being started or failing. It also exposes a method used by PlatformActivator for notifying about OSGi (not blueprint) bundle errors.
 * It also acts as an OSGi event listener for keeping track of bundles that were started by OSGi (includes all bundles in the system).
 * It keeps a single platform status instance, that it keeps updating.
 */
public class PlatformStatusManagerImpl implements PlatformStatusManager, OsgiBundleApplicationContextListener, BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformStatusManagerImpl.class);

    private final PlatformStatus platformStatus = new PlatformStatus();

    @Override
    public PlatformStatus getCurrentStatus() {
        return platformStatus;
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        final String symbolicName = bundleEvent.getBundle().getSymbolicName();

        if (bundleEvent.getType() == BundleEvent.STARTED) {
            LOGGER.debug("Bundle {} started", symbolicName);
            platformStatus.addOSGiStartedBundle(symbolicName);
        } else if (bundleEvent.getType() == BundleEvent.STOPPED) {
            LOGGER.trace("Bundle {} stopped", symbolicName);
            platformStatus.removeOSGiStartedBundle(symbolicName);
        }
    }

    @Override
    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
        if (event instanceof OsgiBundleContextRefreshedEvent) {
            handleContextRefreshedEvent(event);
        } else if (event instanceof OsgiBundleContextClosedEvent) {
            handleContextClosedEvent(event);
        } else if (event instanceof OsgiBundleContextFailedEvent) {
            handleContextFailedEvent((OsgiBundleContextFailedEvent) event);
        } else {
            LOGGER.debug("Received an unknown event type: {}", event);
        }
    }

    /**
     * Used for registering an OSGi error. This is not part of the interface and is used only by the PlatformActivator.
     * @param bundleSymbolicName the symbolic name of the bundle which failed to start
     * @param error the actual error
     */
    public void registerBundleError(String bundleSymbolicName, String error) {
        platformStatus.addBundleError(bundleSymbolicName, error);
    }

    private void handleContextRefreshedEvent(OsgiBundleApplicationContextEvent event) {
        final String symbolicName = event.getBundle().getSymbolicName();

        LOGGER.debug("Received context refreshed event {} from {}", event, symbolicName);
        LOGGER.info("{} ready", symbolicName);

        platformStatus.addStartedBundle(symbolicName);
    }

    private void handleContextClosedEvent(OsgiBundleApplicationContextEvent event) {
        final String symbolicName = event.getBundle().getSymbolicName();

        LOGGER.debug("Received context closed event {} from {}", event, symbolicName);
        LOGGER.info("{} finished", symbolicName);

        platformStatus.removeStartedBundle(symbolicName);
    }

    private void handleContextFailedEvent(OsgiBundleContextFailedEvent event) {
        final String symbolicName = event.getBundle().getSymbolicName();
        final String failureCauseMsg = event.getFailureCause().getMessage();

        LOGGER.debug("Received context closed event {} from {}", event, symbolicName);
        LOGGER.info("{} failed to start due to {}", symbolicName, failureCauseMsg);

        platformStatus.removeStartedBundle(symbolicName);
        platformStatus.addContextError(symbolicName, failureCauseMsg);
    }
}
