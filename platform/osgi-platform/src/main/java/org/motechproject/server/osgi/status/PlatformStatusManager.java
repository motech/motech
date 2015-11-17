package org.motechproject.server.osgi.status;

/**
 * This is an interface for the manager of the platform status.
 * The manager should keep track of the status and return it to callers.
 */
public interface PlatformStatusManager {

    String OSGI_BUNDLES = "osgi-bundles";
    String BLUEPRINT_BUNDLES = "blueprint-bundles";

    /**
     * Used to fetch the current status of the platform.
     * @return the current status of the platform, never null
     */
    PlatformStatus getCurrentStatus();
}
