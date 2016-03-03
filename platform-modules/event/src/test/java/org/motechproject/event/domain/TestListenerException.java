package org.motechproject.event.domain;

public class TestListenerException extends RuntimeException {

    public TestListenerException() {
        super("Thrown intentionally from tests");
    }
}
