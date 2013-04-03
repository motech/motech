package org.motechproject.testing.utils;

import org.apache.commons.lang.StringUtils;

public final class TestContext {

    private TestContext() {
    }

    private static final int DEFAULT_JETTY_PORT = 8080;
    private static final int DEFAULT_TOMCAT_PORT = 9090;
    private static final int DEFAULT_TOMCAT_SHUTDOWN_PORT = 9005;

    public static int getJettyPort() {
        String portStr = System.getProperty("org.osgi.service.http.port");
        return (StringUtils.isNotBlank(portStr)) ? Integer.parseInt(portStr) : DEFAULT_JETTY_PORT;
    }

    public static int getTomcatPort() {
        String portStr = System.getProperty("tomcat.port");
        return (StringUtils.isNotBlank(portStr)) ? Integer.parseInt(portStr) : DEFAULT_TOMCAT_PORT;
    }

    public static int getTomcatShutdownPort() {
        String portStr = System.getProperty("tomcat.shutdown.port");
        return (StringUtils.isNotBlank(portStr)) ? Integer.parseInt(portStr) : DEFAULT_TOMCAT_SHUTDOWN_PORT;
    }
}
