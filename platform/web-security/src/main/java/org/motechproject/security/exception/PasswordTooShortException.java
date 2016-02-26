package org.motechproject.security.exception;

/**
 * Signals that password is shorter than the configured minimal length.
 */
public class PasswordTooShortException extends RuntimeException {

    private static final long serialVersionUID = 4174050819475110888L;

    private final int minLength;

    /**
     * @param minLength the configured minimal length of the password
     */
    public PasswordTooShortException(int minLength) {
        super("Password must be at least " + minLength + " characters long");
        this.minLength = minLength;
    }

    /**
     * @return the configured minimal length of the password
     */
    public int getMinLength() {
        return minLength;
    }
}
