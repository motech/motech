package org.motechproject.server.ex;

/**
 * A RuntimeException that should be thrown when an expected bundle
 * does not resolve or start correctly during the starting of
 * the OSGi framework.
 */
public class CriticalBundleException extends RuntimeException {

    public CriticalBundleException(String message) {
        super(message);
    }
}
