package org.motechproject.admin.ex;

public class NoDbException extends RuntimeException {

    public NoDbException(String message) {
        super(message);
    }
}
