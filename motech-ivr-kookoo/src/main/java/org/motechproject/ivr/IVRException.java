package org.motechproject.ivr;

public class IVRException extends RuntimeException {
    public IVRException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
