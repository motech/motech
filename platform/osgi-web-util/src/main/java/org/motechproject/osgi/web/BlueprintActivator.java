package org.motechproject.osgi.web;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class BlueprintActivator implements BundleActivator {

    private BlueprintApplicationContextTracker contextTracker;

    @Override
    public void start(BundleContext context) {
        contextTracker = new BlueprintApplicationContextTracker(context);
        contextTracker.open();
    }

    public void stop(BundleContext context) {
        contextTracker.close();
    }


}
