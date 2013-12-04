package org.motechproject.tasks.osgi;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * A listener for {@link ChannelService}, effectively serves as a tasks-bundle start handler, that sets up tracking of start/stop events of all other bundles
 */
public class ChannelServiceRegistrationListener implements OsgiServiceRegistrationListener {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelServiceRegistrationListener.class);

    private BundleContext bundleContext;
    private BlueprintApplicationContextTracker tracker;

    @Autowired
    public ChannelServiceRegistrationListener(BundleContext bundleContext) {
        LOG.info("Starting ChannelService registration listener");
        this.bundleContext = bundleContext;
    }

    @Override
    public void registered(Object service, Map serviceProperties) {
        if (service instanceof ChannelService && tracker == null) {
            LOG.info("ChannelService registered, starting BlueprintApplicationContextTracker");

            ChannelService channelService = (ChannelService) service;
            channelService.deregisterAllChannels();

            tracker = new BlueprintApplicationContextTracker(bundleContext, channelService);
            tracker.open(true);
        }
    }

    @Override
    public void unregistered(Object service, Map serviceProperties) {
        if (service instanceof ChannelService && tracker != null) {
            tracker.close();
        }
    }
}
