package org.motechproject.testing.osgi;


import org.apache.commons.lang.StringUtils;

public final class TestContext {

    private static final int DEFAULT_JETTY_PORT = 8080;
    private static final int DEFAULT_TOMCAT_PORT = 9090;
    private static final int DEFAULT_TOMCAT_SHUTDOWN_PORT = 9005;
    private static final int DEFAULT_VERBOICE_PORT = 7080;
    private static final int DEFAULT_KOOKOO_PORT = 7080;
    private static final int DEFAULT_VOXEO_PORT = 9998;
    private static final int DEFAULT_PKG_PORT = 7010;
    private static final int DEFAULT_PKG_TENANT_PORT = 7012;

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

    public static int getPkgTestPort() {
        return getPort("pkg.test.port", DEFAULT_PKG_PORT);
    }

    public static int getPkgTenantTestPort() {
        return getPort("pkg.test.tenant.port", DEFAULT_PKG_TENANT_PORT);
    }

    private static int getPort(String systemProperty, int defaultValue) {
        String portStr = System.getProperty(systemProperty);
        return (StringUtils.isNotBlank(portStr)) ? Integer.parseInt(portStr) : defaultValue;
    }

    private TestContext() {
    }
}
