package org.motechproject.commons.api;

public class SystemIdentityProvider implements IdentityProvider {
    @Override
    public String getIdentity() {
        return System.getProperty("user.name");
    }
}
