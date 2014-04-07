package org.motechproject.testing.osgi.wait;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

public class ContextPublishedWaitCondition implements WaitCondition {

    private final BundleContext bundleContext;
    private final String filter;

    public ContextPublishedWaitCondition(BundleContext bundleContext) {
        this(bundleContext, bundleContext.getBundle().getSymbolicName());
    }

    public ContextPublishedWaitCondition(BundleContext bundleContext, String bundleSymbolicName) {
        this.bundleContext = bundleContext;
        this.filter = String.format("(org.springframework.context.service.namebundleSymbolicName=%s)",
                bundleSymbolicName);
    }

    @Override
    public boolean needsToWait() {
        try {
            ServiceReference[] refs =
                    bundleContext.getServiceReferences(ApplicationContext.class.getName(), filter);
            return refs == null || refs.length == 0;
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("Invalid syntax", e);
        }
    }
}
