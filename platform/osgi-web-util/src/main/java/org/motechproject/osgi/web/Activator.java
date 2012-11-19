package org.motechproject.osgi.web;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    public static final String CONTEXT_CONFIG_LOCATION = "META-INF/osgi/*.xml";

    private HttpServiceTracker tracker;

    @Override
    public void start(BundleContext context) throws Exception {
        this.tracker = new HttpServiceTracker(context);
        this.tracker.open();
        final ServiceReference httpServiceReference = context.getServiceReference(HttpService.class.getName());
        if (httpServiceReference != null) {
            tracker.register((HttpService) context.getService(httpServiceReference));
        }
    }

    public void stop(BundleContext context) throws Exception {
        this.tracker.close();
        tracker.unregister();
    }


}
