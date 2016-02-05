package org.motechproject.metrics.config;

/**
 * Represents configuration for the Graphite reporter.
 */
public class GraphiteReporterConfig extends BaseReporterConfig {
    /**
     * The URI of the Graphite server.
     */
    private String serverUri;

    /**
     * The port number of the Graphite server.
     */
    private int serverPort;

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
