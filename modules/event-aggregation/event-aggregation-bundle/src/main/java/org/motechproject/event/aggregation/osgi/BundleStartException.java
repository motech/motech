package org.motechproject.event.aggregation.osgi;

public class BundleStartException extends RuntimeException {
    public BundleStartException(Exception e) {
        super(e);
    }
}
