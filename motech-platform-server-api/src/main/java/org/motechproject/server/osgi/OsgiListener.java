package org.motechproject.server.osgi;

import org.motechproject.server.startup.StartupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

public class OsgiListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgiListener.class);
    public static final String ADMIN_BUNDLE = "motech-admin";

    private static StartupManager startupManager = StartupManager.getInstance();

    private static OsgiFrameworkService service;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("Starting OSGi framework...");
        getOsgiService(servletContextEvent).start();

        startSystem();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        getOsgiService(servletContextEvent).stop();

        startupManager.stopMonitor();
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

    public static OsgiFrameworkService getOsgiService() {
        return service;
    }

    public static void startSystem() {
        try {
            LOGGER.debug("Starting MoTeCH...");
            startupManager.startup();

            if (startupManager.canLaunchBundles()) {
                LOGGER.info("Monitoring config file...");
                startupManager.startMonitor();

                LOGGER.info("Launching bundles...");
                getOsgiService().startExternalBundles();
            } else {
                LOGGER.warn("Problems with MoTeCH launch. Finding and launching Admin UI bundle to repair errors by user...");

                if (!getOsgiService().startBundle(ADMIN_BUNDLE)) {
                    LOGGER.error("Admin UI bundle not found...");
                    getOsgiService().stop();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
        }
    }

}
