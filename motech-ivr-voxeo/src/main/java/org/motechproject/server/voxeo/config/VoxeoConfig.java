package org.motechproject.server.voxeo.config;

import java.util.Map;

public class VoxeoConfig {
    String serverUrl;

    Map<String, String> applications;

    public String getServerUrl() {
        return serverUrl;
    }

    public String getTokenId(String applicationName) {
        return applications.get(applicationName);
    }
}
