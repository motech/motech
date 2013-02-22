package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Map;

public class HttpServiceTracker extends ServiceTracker {
    private static Logger logger = LoggerFactory.getLogger(ServiceTracker.class);
    private String contextPath;
    private Map<String, String> resourceMapping;
    private BundleContextWrapper bundleContextWrapper;

    public HttpServiceTracker(BundleContext context, Map<String, String> resourceMapping) {
        super(context, HttpService.class.getName(), null);
        this.resourceMapping = resourceMapping;
        this.bundleContextWrapper = new BundleContextWrapper(context);
    }


    @Override
    public Object addingService(ServiceReference serviceReference) {
        Object service = super.addingService(serviceReference);
        register((HttpService) service);
        return service;
    }

    @Override
    public void removedService(ServiceReference ref, Object service) {
        logger.info("Removed service called for " + OsgiStringUtils.nullSafeSymbolicName(ref.getBundle()));
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
            try {
                DispatcherServlet dispatcherServlet = new OsgiDispatcherServlet(context, bundleContextWrapper.getBundleApplicationContext());
                contextPath = WebBundleUtil.getContextPath(context.getBundle());
                dispatcherServlet.setContextClass(MotechOsgiWebApplicationContext.class);
                dispatcherServlet.setContextConfigLocation(WebBundleUtil.getContextLocation(context.getBundle()));
                ClassLoader old = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                    httpService.unregister(contextPath);
                    httpService.registerServlet(contextPath, dispatcherServlet, null, null);
                    if (resourceMapping != null) {
                        for (String key : resourceMapping.keySet()) {
                            logger.debug(String.format("Registering %s = %s for bundle %s ", key, resourceMapping.keySet(), bundleContextWrapper.getCurrentBundleSymbolicName()));
                            httpService.registerResources(key, resourceMapping.get(key), null);
                        }
                    }
                    logger.info(String.format("servlet registered with context path %s for bundle %s", contextPath, OsgiStringUtils.nullSafeSymbolicName(context.getBundle())));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    Thread.currentThread().setContextClassLoader(old);
                }
            } catch (Exception e) {
                throw new ServletRegistrationException(e);
            }
        }
    }

    private void unregister(HttpService service) {
        if (contextPath != null && service != null) {
            service.unregister(contextPath);
            contextPath = null;
            logger.debug("Servlet unregistered");
        }
    }

    private HttpService getHttpService() {
        return bundleContextWrapper.getService(HttpService.class);
    }
}
