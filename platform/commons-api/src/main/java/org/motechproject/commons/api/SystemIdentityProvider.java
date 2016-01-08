package org.motechproject.commons.api;

/**
 * Implementation of {@code IdentityProvider}.
 */
public class SystemIdentityProvider implements IdentityProvider {

    @Override
    public String getIdentity() {
        return System.getProperty("user.name");
    }
}
