package org.motechproject.security.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.felix.http.api.ExtHttpService;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.osgi.web.ServletRegistrationException;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.motechproject.server.ui.UiHttpContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Properties;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "applicationWebSecurityBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/websecurity/api";
    private static final String RESOURCE_URL_MAPPING = "/websecurity";
    private static final String ADMIN_MODE_FILE = "admin-mode.conf";

    private ServiceTracker httpServiceTracker;
    private ServiceTracker uiServiceTracker;

    private static final String MODULE_NAME = "websecurity";

    private static BundleContext bundleContext;
    private static DelegatingFilterProxy filter;

    @Override
    public void start(BundleContext context) {
        bundleContext = context;

        this.httpServiceTracker = new ServiceTracker(context,
                ExtHttpService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((ExtHttpService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((ExtHttpService) service);
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

    public static class WebSecurityApplicationContext extends MotechOsgiWebApplicationContext {

        public WebSecurityApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

    }

    private void serviceAdded(ExtHttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(WebSecurityApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                UiHttpContext httpContext = new UiHttpContext(service.createDefaultHttpContext());

                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                service.registerResources(RESOURCE_URL_MAPPING, "/webapp", httpContext);
                if (!isAdminMode()){
                    filter = new DelegatingFilterProxy("springSecurityFilterChain", dispatcherServlet.getWebApplicationContext());
                    service.registerFilter(filter, "/.*", null,0,httpContext);
                }
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new ServletRegistrationException(e);
        }
    }

    private void serviceRemoved(ExtHttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        service.unregisterFilter(filter);
        logger.debug("Servlet unregistered");
    }

    private void serviceAdded(UIFrameworkService service) {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../websecurity/");
        regData.addAngularModule("motech-web-security");
        regData.addSubMenu("#/users", "manageUsers");
        regData.addSubMenu("#/roles", "manageRoles");
        regData.addI18N("messages", "../websecurity/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            logger.error("Cant read header.html", e);
            throw new ServletRegistrationException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }

        service.registerModule(regData);
        logger.debug("Web Security registered in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(MODULE_NAME);
        logger.debug("Web Security unregistered from ui framework");
    }

    private boolean isAdminMode() {
        Properties adminDetails = new Properties();
        boolean isAdminMode = false;
        File adminMode = new File(String.format("%s/.motech/config/%s", System.getProperty("user.home"), ADMIN_MODE_FILE));
        if (adminMode.exists()) {
            InputStream file = null;
            try {
                file = new FileInputStream(adminMode);
                adminDetails.load(new InputStreamReader(file));
                String am = adminDetails.getProperty("adminMode.mode");
                isAdminMode = Boolean.valueOf(am);
                adminMode.delete();
            } catch (IOException e) {
                logger.debug("Can read file", e);
            } finally {
                IOUtils.closeQuietly(file);
            }
        }
        return isAdminMode;
    }
}
