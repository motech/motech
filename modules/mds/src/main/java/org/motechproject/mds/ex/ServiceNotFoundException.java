package org.motechproject.mds.ex;

/**
 * Signals that service for a corresponding entity was not found.
 * This most likely signals an issue with entities bundle.
 */
public class ServiceNotFoundException extends MdsException {

    private static final long serialVersionUID = -2792362263836791348L;

    public ServiceNotFoundException() {
        super("mds.error.serviceNotAvailable");
    }
}
