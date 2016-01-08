package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.osgi.web.ext.HttpContextFactory;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.util.Map;

/**
 * This is the HttpServiceTracker that will be created by {@link org.motechproject.osgi.web.BlueprintApplicationContextTracker}
 * for bundles that have a Gemini Blueprint context and the <code>Blueprint-Enabled</code> header in their manifest.
 * This class is responsible for tracking the {@link org.osgi.service.http.HttpService}. Once it becomes available,
 * an {@link OSGiDispatcherServlet} is created and registered with service, which means exposing
 * and HTTP endpoint for the bundle. We also create and register an {@link org.motechproject.osgi.web.OSGiDispatcherServlet}
 * with a context built upon the context created by the Gemini Extender. The dispatcher servlet created here
 * allows HTTP access to the bundle, by making its Spring context the parent of the dispatchers context.
 */
public class HttpServiceTracker extends ServiceTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTracker.class);
    private String contextPath;
    private Map<String, String> resourceMapping;
    private BundleContextWrapper bundleContextWrapper;

    public HttpServiceTracker(BundleContext context, Map<String, String> resourceMapping) {
        super(context, HttpService.class.getName(), null);
        this.resourceMapping = resourceMapping;
        this.bundleContextWrapper = new BundleContextWrapper(context);
    }


    @Override
    public Object addingService(ServiceReference ref) {
        LOGGER.info("Adding service called for " + OsgiStringUtils.nullSafeSymbolicName(ref.getBundle()));
        Object service = super.addingService(ref);
        register((HttpService) service);
        return service;
    }

    @Override
    public void removedService(ServiceReference ref, Object service) {
        LOGGER.info("Removed service called for " + OsgiStringUtils.nullSafeSymbolicName(ref.getBundle()));
        unregister((HttpService) service);
        super.removedService(ref, service);
    }

    public void start() {
        register(getHttpService());
        open();
    }


    public void unregister() {
        unregister(getHttpService());
    }

    private void register(HttpService httpService) {
        if (contextPath == null && httpService != null) {
            DispatcherServlet dispatcherServlet = new OSGiDispatcherServlet(context, (ConfigurableWebApplicationContext) bundleContextWrapper.getBundleApplicationContext());
            contextPath = WebBundleUtil.getContextPath(context.getBundle());
            dispatcherServlet.setContextClass(MotechOSGiWebApplicationContext.class);
            dispatcherServlet.setContextConfigLocation(WebBundleUtil.getContextLocation(context.getBundle()));
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                HttpContext httpContext = HttpContextFactory.getHttpContext(httpService.createDefaultHttpContext(), context.getBundle());
                httpService.unregister(contextPath);
                httpService.registerServlet(contextPath, dispatcherServlet, null, httpContext);
                if (resourceMapping != null) {
                    for (String key : resourceMapping.keySet()) {
                        LOGGER.debug(String.format("Registering %s = %s for bundle %s ", key, resourceMapping.keySet(), bundleContextWrapper.getCurrentBundleSymbolicName()));
                        httpService.registerResources(key, resourceMapping.get(key), httpContext);
                    }
                }
                LOGGER.info(String.format("servlet registered with context path %s for bundle %s", contextPath, OsgiStringUtils.nullSafeSymbolicName(context.getBundle())));
            } catch (ServletException e) {
                LOGGER.error("Unable to register dispatcher servlet for {}",
                        bundleContextWrapper.getCurrentBundleSymbolicName(), e);
            } catch (NamespaceException e) {
                LOGGER.error("Unable to register dispatcher servlet for {}, namespace already taken",
                        bundleContextWrapper.getCurrentBundleSymbolicName(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }

    private void unregister(HttpService service) {
        if (contextPath != null && service != null) {
            service.unregister(contextPath);
            contextPath = null;
            LOGGER.debug("Servlet unregistered");
        }
    }

    private HttpService getHttpService() {
        return bundleContextWrapper.getService(HttpService.class);
    }

}
