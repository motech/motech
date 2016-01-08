package org.motechproject.server.ui.ex;

public class AlreadyRegisteredException extends RuntimeException {

    public AlreadyRegisteredException(String msg) {
        super(msg);
    }
}
