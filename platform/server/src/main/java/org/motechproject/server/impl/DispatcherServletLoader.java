package org.motechproject.server.impl;

import org.motechproject.server.api.BundleLoader;
import org.motechproject.server.api.BundleLoadingException;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

public class DispatcherServletLoader implements BundleLoader {

    private ServiceTracker serviceTracker;

    @Override
    public void loadBundle(Bundle bundle) throws BundleLoadingException {
        serviceTracker = new ServiceTracker(bundle.getBundleContext(), ApplicationContext.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                super.removedService(ref, service);
            }
        };
        serviceTracker.open();
    }


}
