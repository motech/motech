package org.motechproject.osgi.web;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Dictionary;

class HttpServiceTracker extends ServiceTracker {
    private static Logger logger = LoggerFactory.getLogger(ServiceTracker.class);

    public static final String HEADER_CONTEXT_PATH = "Context-Path";

    private ServiceReference httpServiceRef;
    private String contextPath;

    public HttpServiceTracker(BundleContext context) {
        super(context, HttpService.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        Object service = super.addingService(serviceReference);
        httpServiceRef = serviceReference;
        register((HttpService) service);
        return service;
    }

    @Override
    public void removedService(ServiceReference ref, Object service) {
        unregister((HttpService) service);
        super.removedService(ref, service);
    }

    public void unregister() {
        if (httpServiceRef != null) {
            unregister((HttpService) context.getService(httpServiceRef));
        }
    }

    private void unregister(HttpService service) {
        if (contextPath != null && service != null) {
            service.unregister(contextPath);
            logger.debug("Servlet unregistered");
            contextPath = null;
        }
    }

    public void register(HttpService service) {
        if (contextPath == null) {
            try {
                DispatcherServlet dispatcherServlet = new OsgiDispatcherServlet(context);
                dispatcherServlet.setContextClass(MotechOsgiWebApplicationContext.class);
                dispatcherServlet.setContextConfigLocation(Activator.CONTEXT_CONFIG_LOCATION);
                ClassLoader old = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                    contextPath = getContextPath(context);
                    service.unregister(contextPath);
                    service.registerServlet(contextPath, dispatcherServlet, null, null);
                    logger.debug("Servlet registered");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    Thread.currentThread().setContextClassLoader(old);
                }
            } catch (Exception e) {
                throw new ServletRegistrationException(e);
            }
        }
    }

    private String getContextPath(BundleContext bundleContext) {
        final Dictionary headers = bundleContext.getBundle().getHeaders();
        if (headers != null && headers.get(HEADER_CONTEXT_PATH) != null) {
            return addRootPath((String) headers.get(HEADER_CONTEXT_PATH));
        }
        return addRootPath(bundleContext.getBundle().getSymbolicName());
    }

    private String addRootPath(String path) {
        return "/" + path;
    }
}
