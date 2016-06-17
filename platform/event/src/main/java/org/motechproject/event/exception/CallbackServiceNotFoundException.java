package org.motechproject.event.exception;

/**
 * Signals that the callback service of the name, specified by {@link org.motechproject.event.MotechEvent#callbackName}
 * could not be found in the running OSGi container.
 */
public class CallbackServiceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7518524483057358206L;

    /**
     * @param callbackName the callback name used in {@link org.motechproject.event.MotechEvent}
     */
    public CallbackServiceNotFoundException(String callbackName) {
        super("Could not find OSGi service, acting as a callback for " + callbackName);
    }
}
