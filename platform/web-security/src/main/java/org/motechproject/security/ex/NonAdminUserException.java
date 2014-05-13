package org.motechproject.security.ex;

public class NonAdminUserException extends Exception {

    public NonAdminUserException(String message) {
        super(message);
    }
}
