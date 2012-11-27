package org.motechproject.sms.smpp.osgi;

import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.osgi.web.ServletRegistrationException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "applicationSmsSmppBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/smssmpp";
    private ServiceTracker tracker;
    private ServiceReference httpService;

    private static BundleContext bundleContext;

    @Override
    public void start(BundleContext context) {
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

    public void stop(BundleContext context) {
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
            dispatcherServlet.setContextClass(SmsSmppApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new ServletRegistrationException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    public static class SmsSmppApplicationContext extends MotechOsgiWebApplicationContext{
        public SmsSmppApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }
    }
}
