package org.motechproject.osgi.web;

import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Map;

public  class HttpServiceTracker extends ServiceTracker {
    private static Logger logger = LoggerFactory.getLogger(ServiceTracker.class);
    private ServiceReference httpServiceRef;
    private String contextPath;
    private Map<String, String> resourceMapping;

    public HttpServiceTracker(BundleContext context, Map<String, String> resourceMapping) {
        super(context, HttpService.class.getName(), null);
        this.resourceMapping = resourceMapping;
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
                contextPath = WebBundleUtil.getContextPath(context.getBundle());
                dispatcherServlet.setContextClass(MotechOsgiWebApplicationContext.class);
                dispatcherServlet.setContextConfigLocation(WebBundleUtil.getContextLocation(context.getBundle()));
                ClassLoader old = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                    service.unregister(contextPath);
                    service.registerServlet(contextPath, dispatcherServlet, null, null);
                    if (resourceMapping!=null) {
                        for (String key : resourceMapping.keySet()){
                            service.registerResources(key, resourceMapping.get(key),null);
                        }
                    }
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
}
