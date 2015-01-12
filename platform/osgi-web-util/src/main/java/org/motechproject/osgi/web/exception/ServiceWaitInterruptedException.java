package org.motechproject.osgi.web.exception;

/**
 * Exception that signals that waiting for an OSGi service
 * was interrupted.
 */
public class ServiceWaitInterruptedException extends RuntimeException {

    private static final long serialVersionUID = -8124279311752423346L;

    public ServiceWaitInterruptedException(String serviceCLassName, InterruptedException cause) {
        super("Interrupted while waiting for the service " + serviceCLassName, cause);
    }
}
