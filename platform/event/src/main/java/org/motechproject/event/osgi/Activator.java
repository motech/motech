package org.motechproject.event.osgi;

import org.motechproject.event.config.ReloadBrokerConfigHandler;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
    private static final String RELOAD_BROKER_CONFIG_TOPIC = "org/motechproject/osgi/event/RELOAD";


    private BlueprintApplicationContextTracker applicationContextTracker;
    private ServiceTracker eventListenerRegistryServiceTracker;

    @Override
    public void start(final BundleContext bundleContext) {

        Hashtable<String, String> properties = new Hashtable<>();
        properties.put(EventConstants.EVENT_TOPIC, RELOAD_BROKER_CONFIG_TOPIC);
        bundleContext.registerService(EventHandler.class.getName(), new ReloadBrokerConfigHandler(), properties);

        eventListenerRegistryServiceTracker = new ServiceTracker(bundleContext, EventListenerRegistryService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object service = super.addingService(reference);
                applicationContextTracker = new BlueprintApplicationContextTracker(bundleContext, (EventListenerRegistryService) service);
                applicationContextTracker.open();
                LOGGER.debug("Started context tracker in event bundle to process beans annotated with @MotechListener");
                return service;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                closeApplicationContextTracker();
            }
        };
        eventListenerRegistryServiceTracker.open();
        LOGGER.debug("Started activator in event bundle");
    }

    @Override
    public void stop(BundleContext context) {
        eventListenerRegistryServiceTracker.close();
        closeApplicationContextTracker();
    }

    private void closeApplicationContextTracker() {
        if (applicationContextTracker != null) {
            applicationContextTracker.close();
        }
    }


}
