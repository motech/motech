package org.motechproject.ivr.osgi;

import org.motechproject.ivr.service.contract.IVRService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public final class IvrBundleListener implements ServiceListener {

    private BundleContext bundleContext;
    private AllIvrBundles allProviderBundles;

    private static class SingletonHolder {
        public static final IvrBundleListener INSTANCE = new IvrBundleListener(AllIvrBundles.instance());
    }

    public static IvrBundleListener instance() {
        return SingletonHolder.INSTANCE;
    }

    private IvrBundleListener() {
    }

    public IvrBundleListener(AllIvrBundles allIvrBundles) {
        this.allProviderBundles = allIvrBundles;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void serviceChanged(ServiceEvent event) {

        ServiceReference serviceReference = event.getServiceReference();
        String[] objectClass = (String[]) serviceReference.getProperty("objectClass");

        if (objectClass.length > 0 && objectClass[0].equals("org.motechproject.ivr.service.IVRService")) {
            String provider = (String) serviceReference.getProperty("IvrProvider");
            if (event.getType() == ServiceEvent.REGISTERED) {
                allProviderBundles.register(provider, (IVRService) bundleContext.getService(serviceReference));
            } else if (event.getType() == ServiceEvent.UNREGISTERING) {
                allProviderBundles.deRegister(provider);
            }
        }
    }
}
