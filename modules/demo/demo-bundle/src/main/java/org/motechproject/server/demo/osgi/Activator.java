package org.motechproject.server.demo.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    private static final String CONTEXT_CONFIG_LOCATION = "applicationDemoBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/demo/api";
    private static final String RESOURCE_URL_MAPPING = "/demo";

    private static final String MODULE_NAME = "demo";

    private ServiceTracker httpServiceTracker;
    private ServiceTracker uiServiceTracker;

    private static BundleContext bundleContext;

    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;

        this.httpServiceTracker = new ServiceTracker(context,
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
        this.httpServiceTracker.open();

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

    public void stop(BundleContext context) throws Exception {
        this.httpServiceTracker.close();
        this.uiServiceTracker.close();
    }

    public static class DemoApplicationContext extends OsgiBundleXmlWebApplicationContext {

        public DemoApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(DemoApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                UiHttpContext httpContext = new UiHttpContext(service.createDefaultHttpContext());

                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, httpContext);
                service.registerResources(RESOURCE_URL_MAPPING, "/webapp", httpContext);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    private void serviceAdded(UIFrameworkService service) {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../demo/");
        regData.addAngularModule("motech-demo");

        regData.addI18N("messages", "../demo/messages/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            logger.error("Cant read header.html", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }

        service.registerModule(regData);
        logger.debug("Demo registered in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(MODULE_NAME);
        logger.debug("Demo unregistered from ui framework");
    }
}
