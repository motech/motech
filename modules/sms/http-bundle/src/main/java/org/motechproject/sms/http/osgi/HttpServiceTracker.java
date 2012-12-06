package org.motechproject.sms.http.osgi;

import org.motechproject.osgi.web.ServletRegistrationException;
import org.motechproject.server.ui.UiHttpContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

class HttpServiceTracker extends ServiceTracker {

    private static Logger logger = LoggerFactory.getLogger(HttpServiceTracker.class);

    private ServletDefinition servletDefinition;

    public HttpServiceTracker(BundleContext context, String serviceClassName, ServiceTrackerCustomizer customizer, ServletDefinition servletDefinition) {
        super(context, serviceClassName, customizer);
        this.servletDefinition = servletDefinition;
    }

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

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(servletDefinition.getContextConfigLocation());
            dispatcherServlet.setContextClass(servletDefinition.getApplicationContextClass());
            ClassLoader old = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                UiHttpContext httpContext = new UiHttpContext(service.createDefaultHttpContext());
                service.registerServlet(servletDefinition.getServletUrlMapping(), dispatcherServlet, null, httpContext);
                service.registerResources(servletDefinition.getResourceUrlMapping(), "/webapp", httpContext);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new ServletRegistrationException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(servletDefinition.getServletUrlMapping());
        logger.debug("Servlet unregistered");
    }
}
