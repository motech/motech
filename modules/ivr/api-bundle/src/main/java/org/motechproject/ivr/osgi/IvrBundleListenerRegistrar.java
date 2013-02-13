package org.motechproject.ivr.osgi;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;

import javax.annotation.PostConstruct;

public class IvrBundleListenerRegistrar implements BundleContextAware {

    private BundleContext bundleContext;


    @PostConstruct
    public void registerListener() {
        if (bundleContext != null) {
            IvrBundleListener ivrBundleListener = IvrBundleListener.instance();
            ivrBundleListener.setBundleContext(bundleContext);
            bundleContext.addServiceListener(ivrBundleListener);
        }
    }


    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
