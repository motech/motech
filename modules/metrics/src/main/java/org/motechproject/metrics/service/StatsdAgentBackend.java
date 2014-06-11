package org.motechproject.metrics.service;

/*
Interface used by StatsdAgentBackendImpl. Required to let user change values of this class from UI.
 */
public interface StatsdAgentBackend {
    String getServerHost();
    void setServerHost(String serverHost);
    int getServerPort();
    void setServerPort(int serverPort);
    String getGraphiteUrl();
    void setGraphiteUrl(String url);
    boolean isGenerateHostBasedStats();
    void setGenerateHostBasedStats(boolean generateHostBasedStats);
    void saveProperties();
}
