package org.motechproject.server.voxeo.osgi;

import org.motechproject.scheduler.context.EventContext;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.EventAnnotationBeanPostProcessor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 *
 */
public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "voxeoResourcesBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/voxeo";
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

    public static class VoxeoApplicationContext extends OsgiBundleXmlWebApplicationContext {

        public VoxeoApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(VoxeoApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }

            // register all annotated handlers
            EventAnnotationBeanPostProcessor.registerHandlers(BeanFactoryUtils.beansOfTypeIncludingAncestors(dispatcherServlet.getWebApplicationContext(), Object.class));
            // create tree objects in the database
            bootStrap();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * responsible for emitting by initialize event (a handler will populate patient and trees in the database for testing)
     */
    private void bootStrap() {
        EventContext.getInstance().getEventRelay().sendEventMessage(new MotechEvent("voxeo.initialize", null));
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }
}
