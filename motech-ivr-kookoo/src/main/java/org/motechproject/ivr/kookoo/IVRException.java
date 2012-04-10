package org.motechproject.ivr.kookoo;

/**
 * Critical IVR failures such as hangup failed.
 */
public class IVRException extends RuntimeException {
    public IVRException(String format, String ... params) {
        super(String.format(format, params));
    }

    public IVRException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
