package org.motechproject.mrs.util;

public class ImplementationNotAvailableException extends ImplementationException {

    private String implName;

    public String getImplName() {
        return implName;
    }

    public ImplementationNotAvailableException(String implName) {
        super(implName + " adapters are not available");
        this.implName = implName;
    }
}
