package org.motechproject.metrics;

//Class used to pass DatagramSocket configuration data from UI to StatsdAgentBackendImpl class
public class StatsdAgentConfigurationData {
    private String serverHost;
    private int serverPort;
    private boolean generateHostBasedStats;
    private String graphiteUrl;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getGraphiteUrl() {
        return graphiteUrl;
    }

    public void setGraphiteUrl(String graphiteUrl) {
        this.graphiteUrl = graphiteUrl;
    }

    public boolean isGenerateHostBasedStats() {
        return generateHostBasedStats;
    }

    public void setGenerateHostBasedStats(boolean generateHostBasedStats) {
        this.generateHostBasedStats = generateHostBasedStats;
    }
}
