package org.motechproject.server.impl;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.bootstrap.impl.BootstrapManagerImpl;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.environment.impl.EnvironmentImpl;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

/**
 * The OsgiListener is ServletContextListener responsible for booting up the OSGi framework which runs Motech.
 * This class runs within the WebApplication context and provides a static getter for the {@link OsgiFrameworkService}.
 * Upon the initialization of the servlet context, this class uses the {@link OsgiFrameworkService} to start up the framework.
 */
public class OsgiListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgiListener.class);

    private static OsgiFrameworkService service;
    private static boolean bootstrapPresent;
    private static BootstrapManager bootstrapManager;
    private static ServletContextEvent servletContextEvent;

    static  {
        bootstrapManager = new BootstrapManagerImpl(buildConfigLocationFileStore(), new EnvironmentImpl());
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        setServletContextEvent(servletContextEvent);
        start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        setServletContextEvent(servletContextEvent);
        getOsgiService().stop();
    }

    private static void setServletContextEvent(ServletContextEvent servletContextEvent) {
        OsgiListener.servletContextEvent = servletContextEvent;
    }

    public static void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
        bootstrapManager.saveBootstrapConfig(bootstrapConfig);
        start();
    }

    private static void start() {
        LOGGER.debug("Starting OSGi framework...");

        BootstrapConfig bootstrapConfig = null;
        try {
            bootstrapConfig = bootstrapManager.loadBootstrapConfig();
            bootstrapPresent = bootstrapConfig != null;
        } catch (RuntimeException e) {
            LOGGER.info("Unable to load bootstrap config: " + e.getMessage());
        }

        if (bootstrapPresent) {
            service = getOsgiService();
            service.init(bootstrapConfig);
            service.start();

            try {
                // reinitialize the servlet, in case we only just started the http bundle
                // will happen if the user provided bootstrap configuration through the UI
                ProxyServlet proxyServlet = ProxyServlet.getInstance();
                if (proxyServlet != null) {
                    proxyServlet.reInit();
                }
            } catch (ServletException e) {
                LOGGER.error("Error while configuring the Proxy Servlet", e);
            }
        }
    }

    public static OsgiFrameworkService getOsgiService() {
        if (service == null) {
            LOGGER.debug("Finding OsgiService instance in context...");
            ServletContext servletContext = servletContextEvent.getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            service = applicationContext.getBean(OsgiFrameworkService.class);
        }
        return service;
    }

    public static boolean isBootstrapPresent() {
        return bootstrapPresent;
    }

    public static boolean isServerBundleActive() {
        return service.isServerBundleStarted();
    }

    private static ConfigLocationFileStore buildConfigLocationFileStore() {
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();

        propertiesConfiguration.setBasePath(System.getProperty("user.home") + "/.motech");
        propertiesConfiguration.setFileName("config-locations.properties");
        try {
            propertiesConfiguration.load();
        } catch (ConfigurationException e) {
            LOGGER.error("Unable to load config locations: " + e.getMessage());
        }
        return new ConfigLocationFileStore(propertiesConfiguration);
    }

    public static boolean inFatalError() {
        return service != null && service.getCurrentPlatformStatus().inFatalError();
    }
}
