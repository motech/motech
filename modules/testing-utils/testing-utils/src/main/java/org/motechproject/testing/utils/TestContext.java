package org.motechproject.testing.utils;

import org.apache.commons.lang.StringUtils;

public final class TestContext {

    private static final int DEFAULT_JETTY_PORT = 8080;
    private static final int DEFAULT_TOMCAT_PORT = 9090;
    private static final int DEFAULT_TOMCAT_SHUTDOWN_PORT = 9005;
    private static final int DEFAULT_VERBOICE_PORT = 7080;
    private static final int DEFAULT_KOOKOO_PORT = 7080;
    private static final int DEFAULT_VOXEO_PORT = 9998;

    public static int getJettyPort() {
        return getPort("org.osgi.service.http.port", DEFAULT_JETTY_PORT);
    }

    public static int getTomcatPort() {
        return getPort("tomcat.port", DEFAULT_TOMCAT_PORT);
    }

    public static int getTomcatShutdownPort() {
        return getPort("tomcat.shutdown.port", DEFAULT_TOMCAT_SHUTDOWN_PORT);
    }

    public static int getVerboicePort() {
        return getPort("verboice.port", DEFAULT_VERBOICE_PORT);
    }

    public static int getKookooPort() {
        return getPort("kookoo.port", DEFAULT_KOOKOO_PORT);
    }

    public static int getVoxeoPort() {
        return getPort("voxeo.port", DEFAULT_VOXEO_PORT);
    }

    private static int getPort(String systemProperty, int defaultValue) {
        String portStr = System.getProperty(systemProperty);
        return (StringUtils.isNotBlank(portStr)) ? Integer.parseInt(portStr) : defaultValue;
    }

    private TestContext() {
    }
}
