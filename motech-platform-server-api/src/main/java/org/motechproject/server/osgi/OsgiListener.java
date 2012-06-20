package org.motechproject.server.osgi;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OsgiListener implements ServletContextListener {

    private static OsgiFrameworkService service;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        getOsgiService(servletContextEvent).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        getOsgiService(servletContextEvent).stop();
    }

    private OsgiFrameworkService getOsgiService(ServletContextEvent servletContextEvent) {
        if (service == null) {
            ServletContext servletContext = servletContextEvent.getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            service = applicationContext.getBean(OsgiFrameworkService.class);
        }
        return service;
    }

    public static OsgiFrameworkService getOsgiService() {
        return service;
    }
}
