package org.motechproject.osgi.web.tracker;

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

    /**
     * Checks whether the given context is still valid (by checking its service reference) and not yet processed.
     * @param serviceReference the service reference for the context
     * @param applicationContext the context to check
     * @return true if the context is invalid or already processed, false otherwise
     */
    protected boolean contextInvalidOrProcessed(ServiceReference serviceReference, ApplicationContext applicationContext) {
        return ApplicationContextServiceReferenceUtils.isNotValid(serviceReference) ||
                contextsProcessed.contains(applicationContext.getId());
    }

    /**
     * Marks the given application context as already processed by this tracker, by saving its id.
     * @param applicationContext the application context to be marked as processed
     */
    protected void markAsProcessed(ApplicationContext applicationContext) {
        contextsProcessed.add(applicationContext.getId());
    }

    /**
     * Undoes marking an application context as processed by this tracked. Its id is removed from the list of processed ids.
     * @param applicationContext the application context to remove from the list of processed contexts
     */
    protected void removeFromProcessed(ApplicationContext applicationContext) {
        contextsProcessed.remove(applicationContext.getId());
    }

    /**
     * Returns an object that should be used as a lock for synchronization of context processing
     * @return a lock for synchronization
     */
    protected Object getLock() {
        return lock;
    }
}
