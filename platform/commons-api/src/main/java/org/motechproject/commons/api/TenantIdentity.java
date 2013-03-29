package org.motechproject.commons.api;

final public class TenantIdentity {
    private TenantIdentity() {
    }

    public static String getTenantId() {
        return System.getProperty("user.name");
    }
}
