package org.motechproject.cmslite.api.osgi;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
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
import java.util.Dictionary;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "META-INF/motech/*.xml";
    private static final String SERVLET_URL_MAPPING = "/cms";
    private static final String HEADER_CONTEXT_PATH = "Context-Path";
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
                httpService = ref;
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
        /*final ServiceReference httpServiceReference = context.getServiceReference(HttpService.class.getName());
        if (httpServiceReference != null) {
            serviceAdded((HttpService) bundleContext.getService(httpServiceReference));
        }*/
    }

    public void stop(BundleContext context) throws Exception {
        this.tracker.close();
        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceRemoved(service);
        }
    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(CmsLiteApiApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                service.registerServlet(getContextPath(), dispatcherServlet, null, null);
                logger.debug("Servlet registered");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getContextPath() {
        final Dictionary headers = bundleContext.getBundle().getHeaders();
        if (headers != null && headers.get(HEADER_CONTEXT_PATH) != null) {
            return addRootPath((String) headers.get(HEADER_CONTEXT_PATH));
        }
        return addRootPath(bundleContext.getBundle().getSymbolicName());
    }

    private String addRootPath(String path) {
        return "/" + path;
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    public static class CmsLiteApiApplicationContext extends OsgiBundleXmlApplicationContext implements ConfigurableWebApplicationContext {

        private ServletContext servletContext;
        private ServletConfig servletConfig;
        private String namespace;

        public CmsLiteApiApplicationContext() {
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
}
