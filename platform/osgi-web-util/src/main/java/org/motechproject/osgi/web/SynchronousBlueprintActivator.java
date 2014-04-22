package org.motechproject.osgi.web;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

public class SynchronousBlueprintActivator implements BundleActivator {

    private static final int DEFAULT_WAIT_TIME = 10000;

    @Override
    public void start(BundleContext context) throws Exception {
        Filter filter = context.createFilter(
                String.format("(&(%s=%s)(org.springframework.context.service.namebundleSymbolicName=%s))",
                    Constants.OBJECTCLASS, ApplicationContext.class.getName(), context.getBundle().getSymbolicName()));

        ServiceTracker contextTracker = new ServiceTracker(context, filter, null);

        contextTracker.waitForService(getWaitTime());
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

    protected int getWaitTime() {
        return DEFAULT_WAIT_TIME;
    }
}
