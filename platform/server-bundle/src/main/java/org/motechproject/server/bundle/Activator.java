package org.motechproject.server.bundle;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.motechproject.server.osgi.OsgiListener;
import org.motechproject.server.startup.StartupManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "applicationPlatformServerBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/server";
    private static final String RESOURCE_URL_MAPPING = String.format("%s/resources", SERVLET_URL_MAPPING);
    private ServiceTracker tracker;
    private ServiceReference httpService;

    private static BundleContext bundleContext;

    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;

        this.tracker = new ServiceTracker(context,
                HttpService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((HttpService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((HttpService) service);
                super.removedService(ref, service);
            }
        };
        this.tracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        this.tracker.close();

        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceRemoved(service);
        }
    }

    public static class ServerApplicationContext extends OsgiBundleXmlApplicationContext implements ConfigurableWebApplicationContext {

        private ServletContext servletContext;
        private ServletConfig servletConfig;
        private String namespace;

        public ServerApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public void setServletConfig(ServletConfig servletConfig) {
            this.servletConfig = servletConfig;
        }

        @Override
        public ServletConfig getServletConfig() {
            return this.servletConfig;
        }

        @Override
        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        @Override
        public String getNamespace() {
            return namespace;
        }

        @Override
        public void setConfigLocation(String configLocation) {
            this.setConfigLocations(new String[] {configLocation});
        }
    }

    public void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(ServerApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                service.registerResources(RESOURCE_URL_MAPPING, "/webapp/resources", null);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
            startMotech();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    private void startMotech() {
        StartupManager startupManager = StartupManager.getInstance();

        logger.debug("Starting MoTeCH...");
        startupManager.startup();

        if (startupManager.canLaunchBundles()) {
            logger.info("Launching MOTECH bundles...");
            OsgiListener.getOsgiService().startMotechBundles();
        }
    }
}
