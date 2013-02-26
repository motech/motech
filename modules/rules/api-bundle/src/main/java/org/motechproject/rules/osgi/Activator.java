package org.motechproject.rules.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {
    private RuleBundleLoader ruleBundleLoader;

    @Override
    public void start(BundleContext context) {
        ruleBundleLoader = new RuleBundleLoader(context, Bundle.STARTING, null);
        ruleBundleLoader.open();
    }

    public void stop(BundleContext context) {
        this.ruleBundleLoader.close();
    }


}
