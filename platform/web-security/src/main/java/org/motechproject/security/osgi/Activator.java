package org.motechproject.security.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.felix.http.api.ExtHttpService;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.osgi.web.exception.ServletRegistrationException;
import org.motechproject.osgi.web.ext.ApplicationEnvironment;
import org.motechproject.osgi.web.ext.HttpContextFactory;
import org.motechproject.security.filter.MotechDelegatingFilterProxy;
import org.motechproject.security.service.MotechProxyManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * The Spring security activator is used to register
 * the spring security filter, dispatcher servlet, and
 * MotechProxyManager, which is necessary for supporting dynamic
 * security. When initializing the security chain, the DB
 * will be consulted for security configuration, if it's not
 * there then the default security filter from the securityContext
 * file is used.
 *
 */
public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "classpath:META-INF/osgi/applicationWebSecurityBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/websecurity/api";
    private static final String RESOURCE_URL_MAPPING = "/websecurity";

    private ServiceTracker httpServiceTracker;
    private ServiceTracker uiServiceTracker;

    private static final String MODULE_NAME = "websecurity";

    private static BundleContext bundleContext;
    private static DelegatingFilterProxy filter;

    public static void setBundleContext(BundleContext context) {
        bundleContext = context;
    }

    @Override
    public void start(BundleContext context) {
        setBundleContext(context);

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

    /**
     * Initializes the security chain by fetching the proxy manager,
     * registers the security filter and spring dispatcher servlet.
     */
    private void serviceAdded(ExtHttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(WebSecurityApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

                HttpContext httpContext = HttpContextFactory.getHttpContext(service.createDefaultHttpContext(),
                        bundleContext.getBundle(), new ApplicationEnvironment());

                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                service.registerResources(RESOURCE_URL_MAPPING, "/webapp", httpContext);
                logger.debug("Servlet registered");

                filter = new MotechDelegatingFilterProxy("springSecurityFilterChain", dispatcherServlet.getWebApplicationContext());
                MotechProxyManager proxyManager = dispatcherServlet.getWebApplicationContext().getBean(MotechProxyManager.class);
                proxyManager.initializeProxyChain();
                service.registerFilter(filter, "/.*", null, 0, httpContext);
                logger.debug("Filter registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new ServletRegistrationException(e);
        }
    }

    private void serviceRemoved(ExtHttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");

        service.unregisterFilter(filter);
        logger.debug("Filter unregistered");
    }

    private void serviceAdded(UIFrameworkService service) {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../websecurity/index.html");
        regData.addAngularModule("motech-web-security");
        regData.addSubMenu("#/users", "security.manageUsers");
        regData.addSubMenu("#/roles", "security.manageRoles");
        regData.addSubMenu("#/permissions", "security.managePermissions");
        regData.addI18N("messages", "../websecurity/messages/");
        regData.setBundle(bundleContext.getBundle());

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

}
