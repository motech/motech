package ${groupId}.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.osgi.web.ServletRegistrationException;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.osgi.web.UiHttpContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Activator implements BundleActivator {

    private static final String CONTEXT_CONFIG_LOCATION = "appContext.xml";

    private static final String MODULE_LINK = "${artifactId}";

    private static final String SERVLET_URL_MAPPING = "/${artifactId}/api";
    private static final String RESOURCE_URL_MAPPING = "/${artifactId}";
    public static final String ANGULAR_MODULE = "${artifactId}";
    public static final String WEBAPP_DIRECTORY = "/webapp";

    private ServiceTracker httpServiceTracker;
    private ServiceTracker uiServiceTracker;

    private static BundleContext bundleContext;

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) {
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

    public void stop(BundleContext context) {
        this.httpServiceTracker.close();
        this.uiServiceTracker.close();
    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(YourApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                UiHttpContext httpContext = new UiHttpContext(service.createDefaultHttpContext());
                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, httpContext);
                service.registerResources(RESOURCE_URL_MAPPING, WEBAPP_DIRECTORY, httpContext);
                logger.debug("Dispatcher servlet registered for your module");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new ServletRegistrationException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Dispatcher servlet unregistered for your module");
    }

    private void serviceAdded(UIFrameworkService service) {
        String resourceRoot = ".." + RESOURCE_URL_MAPPING;
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_LINK);
        regData.setUrl(resourceRoot);
        regData.addAngularModule(ANGULAR_MODULE);
        regData.addI18N("messages", resourceRoot + "/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            throw new MotechException("Cant read header.html", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }

        service.registerModule(regData);
        logger.debug("Your module is registered in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(MODULE_LINK);
        logger.debug("Unregistered your module from ui framework");
    }

    public static class YourApplicationContext extends MotechOsgiWebApplicationContext {

        public YourApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }
    }
}
