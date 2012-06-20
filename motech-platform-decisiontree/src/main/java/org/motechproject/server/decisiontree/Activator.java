package org.motechproject.server.decisiontree;

import org.motechproject.context.Context;
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
    private static final String CONTEXT_CONFIG_LOCATION = "classpath:applicationDecisionTree.xml";
    private static final String SERVLET_URL_MAPPING = "/tree";
    private ServiceTracker tracker;
    private ServiceReference httpService;


    @Override
    public void start(BundleContext context) throws Exception {
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
        httpService = context.getServiceReference(HttpService.class.getName());
        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceAdded(service);
        }
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
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }

            // I need to access Context so maven-bundle-plugin lists it in my manifest.
            // If I don't actually need to reference it then this access should be removed and we should
            // explicitly list the dependency in the bundle plugin config
            Context context = Context.getInstance();
            logger.info("Using Context: " + context.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }
}
