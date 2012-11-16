package org.motechproject.osgi.web;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Dictionary;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "META-INF/osgi/*.xml";
    private static final String HEADER_CONTEXT_PATH = "Context-Path";
    private ServiceTracker tracker;
    private ServiceReference httpService;

    private String contextPath;

    @Override
    public void start(BundleContext context) throws Exception {

        this.tracker = new ServiceTracker(context, HttpService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                httpService = ref;
                serviceAdded((HttpService) service, context);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((HttpService) service);
                super.removedService(ref, service);
            }
        };
        this.tracker.open();
        /*final ServiceReference httpServiceReference = context.getServiceReference(HttpService.class.getName());
        if (httpServiceReference != null) {
            serviceAdded((HttpService) bundleContext.getService(httpServiceReference));
        }*/
    }

    public void stop(BundleContext context) throws Exception {
        this.tracker.close();
        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceRemoved(service);
        }
    }

    private void serviceAdded(HttpService service, BundleContext bundleContext) {
        try {
            DispatcherServlet dispatcherServlet = new OsgiDispatcherServlet(bundleContext);
            dispatcherServlet.setContextClass(MotechOsgiWebApplicationContext.class);
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                contextPath = getContextPath(bundleContext);
                service.registerServlet(contextPath, dispatcherServlet, null, null);
                logger.debug("Servlet registered");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getContextPath(BundleContext bundleContext) {
        final Dictionary headers = bundleContext.getBundle().getHeaders();
        if (headers != null && headers.get(HEADER_CONTEXT_PATH) != null) {
            return addRootPath((String) headers.get(HEADER_CONTEXT_PATH));
        }
        return addRootPath(bundleContext.getBundle().getSymbolicName());
    }

    private String addRootPath(String path) {
        return "/" + path;
    }

    private void serviceRemoved(HttpService service) {
        if (contextPath != null && service != null) {
            service.unregister(contextPath);
            logger.debug("Servlet unregistered");
        }
    }

    private static class OsgiDispatcherServlet extends DispatcherServlet {
        private BundleContext bundleContext;

        public OsgiDispatcherServlet(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }

        @Override
        protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
            if (wac instanceof MotechOsgiWebApplicationContext) {
                MotechOsgiWebApplicationContext wc = (MotechOsgiWebApplicationContext) wac;
                wc.setBundleContext(bundleContext);
            }
            super.postProcessWebApplicationContext(wac);
        }
    }
}
