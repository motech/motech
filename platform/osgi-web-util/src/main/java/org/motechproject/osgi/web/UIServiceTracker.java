package org.motechproject.osgi.web;

import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * A tracker created for each bundle with the <code>Blueprint-Enabled</code> header in its manifest.
 * This tracker will track the {@link org.motechproject.osgi.web.UIFrameworkService}, once it becomes
 * active it registers the bundle with it - thanks to this the {@link org.motechproject.osgi.web.ModuleRegistrationData}
 * beans defined in the modules will be respected and will make the module incorporated into the UI.
 */
public class UIServiceTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(UIServiceTracker.class);

    private ModuleRegistrationData moduleRegistrationData;

    /**
     * Constructs the tracker instance for a bundle.
     * @param context the context of the bundle for which this tracker should work
     * @param moduleRegistrationData the module registration data for the bundle that will be used when registering with the UI service
     */
    public UIServiceTracker(BundleContext context, ModuleRegistrationData moduleRegistrationData) {
        super(context, UIFrameworkService.class.getName(), null);

        this.moduleRegistrationData = moduleRegistrationData;
        Bundle bundle = context.getBundle();
        BundleHeaders headers = new BundleHeaders(bundle);

        moduleRegistrationData.setBundle(bundle);
        moduleRegistrationData.setResourcePath(headers.getResourcePath());

        LOGGER.debug("Constructed UI tracker for {}", bundle.getSymbolicName());
    }

    /**
     * Constructs the tracker instance for a bundle.
     * @param wrapper a wrapper of the context of the bundle for which this tracker should work
     * @param moduleRegistrationData the module registration data for the bundle that will be used when registering with the UI service
     */
    public UIServiceTracker(BundleContextWrapper wrapper, ModuleRegistrationData moduleRegistrationData) {
        this(wrapper.getBundleContext(), moduleRegistrationData);
    }

    @Override
    public Object addingService(ServiceReference ref) {
        Object service = super.addingService(ref);
        register((UIFrameworkService) service);
        return service;
    }

    @Override
    public void removedService(ServiceReference ref, Object service) {
        serviceRemoved((UIFrameworkService) service);
        super.removedService(ref, service);
    }

    @PostConstruct
    public void start() {
        registerServiceIfAvailable();
        open();
    }

    private void registerServiceIfAvailable() {
        ServiceReference serviceReference = context.getServiceReference(UIFrameworkService.class.getName());
        if (serviceReference != null) {
            register((UIFrameworkService) context.getService(serviceReference));
        }
    }

    private void register(UIFrameworkService service) {
        if (service.isModuleRegistered(moduleRegistrationData.getModuleName())) {
            return;
        }

        service.registerModule(moduleRegistrationData);
        LOGGER.debug(String.format("%s registered in UI framework", moduleRegistrationData.getModuleName()));
    }


    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(moduleRegistrationData.getModuleName());
        LOGGER.debug(String.format("%s unregistered from ui framework", moduleRegistrationData.getModuleName()));
    }
}
