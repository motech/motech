package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that service for a corresponding entity was not found.
 * This most likely signals an issue with entities bundle.
 */
public class ServiceNotFoundException extends MdsException {

    private static final long serialVersionUID = -2792362263836791348L;

    /**
     * @param serviceClassName class name of the service that was not found
     */
    public ServiceNotFoundException(String serviceClassName) {
        super("Unable to find service " + serviceClassName, null, "mds.error.serviceNotAvailable");
    }
}
