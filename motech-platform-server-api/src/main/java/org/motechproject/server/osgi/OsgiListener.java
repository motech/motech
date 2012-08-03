package org.motechproject.server.osgi;

import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.startup.StartupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OsgiListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgiListener.class);
    public static final String ADMIN_BUNDLE = "motech-admin";

    private static OsgiFrameworkService service;

    private StartupManager startupManager = StartupManager.getInstance();
    private ConfigFileMonitor configFileMonitor;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("Starting OSGi framework...");
        getOsgiService(servletContextEvent).start();

        LOGGER.debug("Starting MoTeCH...");
        startupManager.startup();

        if (startupManager.canLaunchBundles()) {
            LOGGER.info("Monitoring config file...");
            getConfigFileMonitor(servletContextEvent).monitor();

            LOGGER.info("Launching bundles...");
            getOsgiService().startExternalBundles();
        } else {
            LOGGER.warn("Problems with MoTeCH launch. Finding and launching Admin UI bundle to repair errors by user...");

            if (!getOsgiService().startBundle(ADMIN_BUNDLE)) {
                LOGGER.error("Admin UI bundle not found...");
                getOsgiService().stop();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        getOsgiService(servletContextEvent).stop();
    }

    private OsgiFrameworkService getOsgiService(ServletContextEvent servletContextEvent) {
        if (service == null) {
            LOGGER.debug("Finding OsgiService instance in context...");
            ServletContext servletContext = servletContextEvent.getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            service = applicationContext.getBean(OsgiFrameworkService.class);
        }
        return service;
    }

    private ConfigFileMonitor getConfigFileMonitor(ServletContextEvent servletContextEvent) {
        if (configFileMonitor == null) {
            LOGGER.debug("Finding ConfigFileMonitor instance in context...");
            ServletContext servletContext = servletContextEvent.getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            configFileMonitor = applicationContext.getBean(ConfigFileMonitor.class);
        }
        return configFileMonitor;
    }

    public static OsgiFrameworkService getOsgiService() {
        return service;
    }

}
