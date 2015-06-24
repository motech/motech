package org.motechproject.testing.utils;


import org.apache.commons.lang.StringUtils;

/**
 * This class provides port information during tests. No ports should be hardcoded within tests,
 * they should be retrieved using this class, so that there are no conflicts in parallel build environments.
 */
public final class TestContext {

    private static final int DEFAULT_JETTY_PORT = 8080;
    private static final int DEFAULT_TOMCAT_PORT = 9090;
    private static final int DEFAULT_TOMCAT_SHUTDOWN_PORT = 9005;
    private static final int DEFAULT_PKG_PORT = 7010;
    private static final int DEFAULT_PKG_TENANT_PORT = 7012;

    /**
     * Returns the port used by the Jetty HTTP server during bundle integration tests. This will always return the
     * port used during the build, even if Jetty will not be available given that test.
     * This is specified using the {@code org.osgi.service.http.port} variable.
     * @return the port used by Jetty during Bundle Integration Tests
     */
    public static int getJettyPort() {
        return getPort("org.osgi.service.http.port", DEFAULT_JETTY_PORT);
    }

    /**
     * Returns the port under which Tomcat will run during integration tests that launch an instance of it. Such tests exists
     * in the server module and test Motech running in an environment very closely resembling a typical deployment.
     * This is specified using the {@code tomcat.port} variable.
     * @return the port used by the T7 maven plugin during integration tests
     */
    public static int getTomcatPort() {
        return getPort("tomcat.port", DEFAULT_TOMCAT_PORT);
    }

    /**
     * Returns the shutdown port under which Tomcat will listen for the shutdown signal during integration tests
     * that launch an instance of it. Such tests exist in the server module and test Motech running in an environment
     * very closely resembling a typical deployment. The shutdown port should not be directly interacted with during
     * integration tests under normal circumstances. This is specified using the {@code tomcat.shutdown.port} variable.
     * @return the shutcown port used by the T7 maven plugin during integration tests
     */
    public static int getTomcatShutdownPort() {
        return getPort("tomcat.shutdown.port", DEFAULT_TOMCAT_SHUTDOWN_PORT);
    }

    /**
     * Returns the port under which the main Motech instance is started during packaging integration tests (deb & rpm).
     * This is specified using the {@code pkg.test.port} variable.
     * @return the port for the main Motech instance during packaging tests
     */
    public static int getPkgTestPort() {
        return getPort("pkg.test.port", DEFAULT_PKG_PORT);
    }

    /**
     * Returns the port under which the secondary Motech tenant instance is started during packaging integration tests (deb & rpm).
     * This is specified using the {@code pkg.test.tenant.port} variable.
     * @return the port for the secondary tenant Motech instance during packaging tests
     */
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
