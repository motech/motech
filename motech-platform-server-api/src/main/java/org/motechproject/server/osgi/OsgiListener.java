package org.motechproject.server.osgi;

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

    private static OsgiFrameworkService service;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("Starting OSGi framework...");
        getOsgiService(servletContextEvent).start();

        StartupManager manager = findStartupManager(servletContextEvent);
        LOGGER.debug("Starting MoTeCH...");
        manager.startup();

        if (manager.canLaunchBundles()) {
            LOGGER.info("Launching bundles...");
            getOsgiService(servletContextEvent).launchBundles();
        } else {
            LOGGER.warn("Problems with MoTeCH launch. Finding and launching Admin UI bundle to repair errors by user...");

            if (!getOsgiService(servletContextEvent).launchBundle("admin-UI")) {
                LOGGER.error("Admin UI bundle not found...");
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

    private StartupManager findStartupManager(ServletContextEvent servletContextEvent) {
        LOGGER.debug("Finding StartupManager instance in context...");
        ServletContext servletContext = servletContextEvent.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return applicationContext.getBean("startupManager", StartupManager.class);
    }

    public static OsgiFrameworkService getOsgiService() {
        return service;
    }
}
