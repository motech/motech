package org.motechproject.security.exception;

/**
 * Signals that the password didn't pass validation.
 */
public class PasswordValidatorException extends RuntimeException {

    public PasswordValidatorException(String msg) {
        super(msg);
    }
}
