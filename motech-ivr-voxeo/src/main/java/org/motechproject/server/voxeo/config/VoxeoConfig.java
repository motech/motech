package org.motechproject.server.voxeo.config;

import java.util.Map;

/**
 * Configuration parameters for Voxeo IVR system.
 */
public class VoxeoConfig {
    String serverUrl;

    Map<String, String> applications;

    /**
     * Voxeo server end point.
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * get Token for givin application name, typically read from JSON.
     * @param applicationName
     * @return
     */
    public String getTokenId(String applicationName) {
        return applications.get(applicationName);
    }
}
