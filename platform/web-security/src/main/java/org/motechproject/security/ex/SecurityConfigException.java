package org.motechproject.security.ex;

/**
 * A runtime exception thrown when the security config does not
 * pass validation constraints required in order to construct
 * a new security chain. Ideally should not be thrown as
 * the UI should not allow invalid data to be submitted.
 */
public class SecurityConfigException extends RuntimeException {

    public SecurityConfigException(String message) {
        super(message);
    }
}
