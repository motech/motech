package org.motechproject.server.osgi.status.impl;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.motechproject.server.osgi.status.PlatformStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pawel on 21.04.15.
 */
public class PlatformStatusListener implements OsgiBundleApplicationContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformStatusListener.class);

    private final PlatformStatus platformStatus = new PlatformStatus();

    public PlatformStatus getCurrentStatus() {
        return platformStatus;
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
