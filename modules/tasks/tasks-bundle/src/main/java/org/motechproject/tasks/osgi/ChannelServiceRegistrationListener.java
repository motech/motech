package org.motechproject.tasks.osgi;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ChannelServiceRegistrationListener implements OsgiServiceRegistrationListener {
    private BundleContext bundleContext;
    private BlueprintApplicationContextTracker tracker;

    @Autowired
    public ChannelServiceRegistrationListener(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void registered(Object service, Map serviceProperties) {
        if (service instanceof ChannelService && tracker == null) {
            tracker = new BlueprintApplicationContextTracker(bundleContext, (ChannelService) service);
            tracker.open();
        }
    }

    @Override
    public void unregistered(Object service, Map serviceProperties) {
        if (service instanceof ChannelService && tracker != null) {
            tracker.close();
        }
    }
}
