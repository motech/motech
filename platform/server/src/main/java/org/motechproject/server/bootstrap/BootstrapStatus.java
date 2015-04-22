package org.motechproject.server.bootstrap;

import org.motechproject.server.osgi.status.PlatformStatus;

import java.io.Serializable;

/**
 * Transports information about the state of the platform at startup.
 */
public class BootstrapStatus implements Serializable {

    private static final long serialVersionUID = -8488315045279371605L;

    public BootstrapStatus() {
    }

    public BootstrapStatus(PlatformStatus platformStatus, boolean bundleErrorOccurred) {
        this.platformStatus = platformStatus;
        this.bundleErrorOccurred = bundleErrorOccurred;
    }

    private PlatformStatus platformStatus;
    private boolean bundleErrorOccurred;

    public PlatformStatus getPlatformStatus() {
        return platformStatus;
    }

    public void setPlatformStatus(PlatformStatus platformStatus) {
        this.platformStatus = platformStatus;
    }

    public boolean isBundleErrorOccurred() {
        return bundleErrorOccurred;
    }

    public void setBundleErrorOccurred(boolean bundleErrorOccurred) {
        this.bundleErrorOccurred = bundleErrorOccurred;
    }
}
