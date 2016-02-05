package org.motechproject.osgi.web.osgi;

import org.motechproject.osgi.web.tracker.impl.BlueprintApplicationContextTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The bundle activator used by this (osgi-web-util) module.
 * It launches a {@link BlueprintApplicationContextTracker} that tracks blueprint contexts
 * and does all the processing required for making those contexts into fully functional modules.
 *
 * @see BlueprintApplicationContextTracker
 */
public class BlueprintActivator implements BundleActivator {

    private BlueprintApplicationContextTracker contextTracker;

    @Override
    public void start(BundleContext context) {
        contextTracker = new BlueprintApplicationContextTracker(context);
        contextTracker.open();
    }

    @Override
    public void stop(BundleContext context) {
        contextTracker.close();
    }
}
