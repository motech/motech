package org.motechproject.server.osgi.status.impl;

import org.motechproject.server.osgi.status.PlatformStatus;
import org.motechproject.server.osgi.status.PlatformStatusManager;

/**
 * Created by pawel on 21.04.15.
 */
public class PlatformStatusManagerImpl implements PlatformStatusManager {

    private final PlatformStatusListener platformStatusListener;

    public PlatformStatusManagerImpl(PlatformStatusListener platformStatusListener) {
        this.platformStatusListener = platformStatusListener;
    }

    @Override
    public PlatformStatus getCurrentStatus() {
        return platformStatusListener.getCurrentStatus();
    }
}
