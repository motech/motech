package org.motechproject.event.aggregation.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.motechproject.server.ui.UiHttpContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Activator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceTracker tracker;
    private ServiceReference httpService;

    private static BundleContext bundleContext;
    private ServiceTracker uiServiceTracker;

    private static final String MODULE_NAME = "event-aggregation";
    private static final String CONTEXT_CONFIG_LOCATION = "META-INF/osgi/applicationEventAggregationBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/event-aggregation/api";
    private static final String RESOURCE_URL_MAPPING = "/event-aggregation";

    @Override
    public void start(BundleContext context) {

        bundleContext = context;

        this.tracker = new ServiceTracker(context, HttpService.class.getName(), null) {

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

        this.uiServiceTracker = new ServiceTracker(context,
            UIFrameworkService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((UIFrameworkService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((UIFrameworkService) service);
                super.removedService(ref, service);
            }
        };
        this.uiServiceTracker.open();

    }

    public void stop(BundleContext context) {
        this.tracker.close();
        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceRemoved(service);
        }
    }

    public static class EventAggregationApplicationContext extends MotechOsgiWebApplicationContext {

        public EventAggregationApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(EventAggregationApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);

                UiHttpContext httpContext = new UiHttpContext(service.createDefaultHttpContext());
                service.registerResources(RESOURCE_URL_MAPPING, "/webapp", httpContext);

                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (NamespaceException e) {
            throw new BundleStartException(e);
        } catch (ServletException e) {
            throw new BundleStartException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    private void serviceAdded(UIFrameworkService service) {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../event-aggregation/");
        regData.addAngularModule("motech-event-aggregation");

        regData.addI18N("messages", "../event-aggregation/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            logger.error("Cant read header.html", e);
            throw new BundleStartException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
        service.registerModule(regData);
        logger.debug("Event Aggregation registered in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(MODULE_NAME);
        logger.debug("Event Aggregation unregistered from ui framework");
    }
}
