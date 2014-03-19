package org.motechproject.osgi.web;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for every class that wishes to track Spring application context.
 * Contains a methods that help with synchronous processing.
 */
public abstract class ApplicationContextTracker extends ServiceTracker {

    private Set<String> contextsProcessed = Collections.synchronizedSet(new HashSet<String>());

    private final Object lock = new Object();

    public ApplicationContextTracker(BundleContext context) {
        super(context, ApplicationContext.class.getName(), null);
    }

    protected boolean contextInvalidOrProcessed(ServiceReference serviceReference, ApplicationContext applicationContext) {
        return ApplicationContextServiceReferenceUtils.isNotValid(serviceReference)||
                contextsProcessed.contains(applicationContext.getId());
    }

    protected void markAsProcessed(ApplicationContext applicationContext) {
        contextsProcessed.add(applicationContext.getId());
    }

    protected void removeFromProcessed(ApplicationContext applicationContext) {
        contextsProcessed.remove(applicationContext.getId());
    }

    protected Object getLock() {
        return lock;
    }
}
