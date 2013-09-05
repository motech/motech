package org.motechproject.event.metrics;

/*
Interface used by StatsdAgentBackendImpl. Required to let user change values of this class from UI.
 */
public interface StatsdAgentBackend {
    String getServerHost();
    void setServerHost(String serverHost);
    int getServerPort();
    void setServerPort(int serverPort);
    boolean isGenerateHostBasedStats();
    void setGenerateHostBasedStats(boolean generateHostBasedStats);
    void saveProperties();
}
